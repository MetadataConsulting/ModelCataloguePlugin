package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors

@Log4j
class DraftChain extends PublishingChain {

    private final DraftContext strategy

    private DraftChain(CatalogueElement published, DraftContext strategy) {
        super(published)
        this.strategy = strategy
    }

    static DraftChain create(CatalogueElement published, DraftContext strategy) {
        return new DraftChain(published, strategy)
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        if (!strategy.forceNew) {
            if (isDraft(published) || isUpdatingInProgress(published)) {
                return published
            }

            if (published.latestVersionId) {
                def existingDrafts = published.class.findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.DRAFT, [sort: 'versionNumber', order: 'desc'])
                for (existing in existingDrafts) {
                    if (existing.id != published.id) {
                        return existing
                    }
                }
            }
        }

        // only the first element in the chain can be forced
        strategy.stopForcingNew()

        startUpdating()

        for (CatalogueElement element in required) {
            if (!isDraft(element)) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (element.id in processed || isUpdatingInProgress(element) || isDeprecated(element)) {
                    continue
                }
                processed << element.id
                CatalogueElement draft = element.createDraftVersion(publisher, strategy)
                if (draft.hasErrors()) {
                    log.warn(FriendlyErrors.printErrors("Draft version $draft has errors", draft.errors))
                    return rejectDraftDependency(draft)
                }
            }
        }
        // TODO: the creating relationships must happen in two phase, first everything we need is turned into draft
        // than the new relationships will link these existing drafts
        return createDraft(publisher)
    }

    private CatalogueElement createDraft(Publisher<CatalogueElement> archiver) {
        if (!published.latestVersionId) {
            published.latestVersionId = published.id
            FriendlyErrors.failFriendlySave(published)
        }

        if (published.archived) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.cannot.be.archived', 'Cannot create draft version from deprecated element!')
            return published
        }

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(published.class.name) as GrailsDomainClass

        CatalogueElement draft = published.class.newInstance()

        for (prop in domainClass.persistentProperties) {
            if (!prop.association) {
                draft.setProperty(prop.name, published.getProperty(prop.name))
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


        draft.addToSupersedes(published, skipUniqueChecking: true)

        strategy.delayRelationshipCopying(draft, published)

        published.afterDraftPersisted(draft)

        if (published.status == ElementStatus.DRAFT) {
            archiver.archive(published)
        }

        draft.status = ElementStatus.DRAFT
        draft.save()
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

    private static isDeprecated(CatalogueElement element) {
        element.status == ElementStatus.DEPRECATED
    }

}
