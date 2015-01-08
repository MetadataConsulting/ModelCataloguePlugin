package org.modelcatalogue.core.publishing

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.Relationship

class DraftChain extends PublishingChain {

    private final boolean force

    private DraftChain(CatalogueElement published, boolean force) {
        super(published)
        this.force = force
    }

    static DraftChain create(CatalogueElement published, boolean force) {
        return new DraftChain(published, force)
    }

    CatalogueElement run(Publisher<CatalogueElement> publisher) {
        if (!force) {
            if (isDraft(published) || isUpdatingInProgress(published)) {
                return published
            }
        }


        if (published.latestVersionId) {
            def existingDrafts = published.class.findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.DRAFT, [sort: 'versionNumber', order: 'desc'])
            for (existing in existingDrafts) {
                if (existing.id != published.id) {
                    return existing
                }
            }
        }

        startUpdating()

        for (CatalogueElement element in required) {
            if (!isDraft(element)) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (element.id in processed || isUpdatingInProgress(element)) {
                    continue
                }
                processed << element.id
                CatalogueElement draft = element.createDraftVersion(publisher, force)
                if (draft.hasErrors()) {
                    return rejectDraftDependency(draft)
                }
            }
        }
        return createDraft(publisher)
    }

    private CatalogueElement createDraft(Publisher<CatalogueElement> archiver) {
        if (!published.latestVersionId) {
            published.latestVersionId = published.id
            published.save(failOnError: true)
        }

        if (published.archived) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.cannot.be.archived', 'Cannot create draft version from deprecated element!')
            return published
        }

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(published.class.name) as GrailsDomainClass

        CatalogueElement draft = published.class.newInstance()

        for (prop in domainClass.persistentProperties) {
            if (!prop.association) {
                draft.setProperty(prop.name, published[prop.name])
            }
        }

        draft.versionNumber = (published.class.findByLatestVersionId(published.latestVersionId, [sort: 'versionNumber', order: 'desc'])?.versionNumber ?: 1) + 1
        draft.versionCreated = new Date()

        draft.latestVersionId = published.latestVersionId ?: published.id
        draft.status = ElementStatus.UPDATED
        draft.dateCreated = published.dateCreated

        draft.beforeDraftPersisted()

        if (!draft.save()) {
            return draft
        }

        restoreStatus()


        draft.addToSupersedes(published)

        draft = addRelationshipsToDraft(draft, published)

        published.afterDraftPersisted(draft)

        if (published.status == ElementStatus.DRAFT) {
            archiver.archive(published)
        }

        draft.status = ElementStatus.DRAFT
        draft.save()
    }

    private static <E extends CatalogueElement> E addRelationshipsToDraft(E draft, E element) {
        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            draft.createLinkFrom(r.source, r.relationshipType)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            draft.createLinkTo(r.destination, r.relationshipType)
        }

        draft
    }


    private CatalogueElement rejectDraftDependency(CatalogueElement element) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.cannot.create.draft.dependency', "Cannot create draft of dependency ${element}, please, resolve the issue first. You'll see more details when you try to create draft manualy")
        published
    }

    private CatalogueElement rejectRequiredDependency(CatalogueElement element) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.required.draft.dependency', "Dependency ${element} is not draft. Please, create draft for it first.")
        published
    }

    private static isDraft(CatalogueElement element) {
        element.status == ElementStatus.DRAFT
    }

}
