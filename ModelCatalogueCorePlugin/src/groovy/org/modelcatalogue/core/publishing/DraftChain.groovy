package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

@Log4j
class DraftChain extends PublishingChain {

    private final DraftContext context

    private DraftChain(CatalogueElement published, DraftContext context) {
        super(published)
        this.context = context
    }

    static DraftChain create(CatalogueElement published, DraftContext strategy) {
        return new DraftChain(published, strategy)
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        if (!context.forceNew) {
            if (isDraft(published) || isUpdatingInProgress(published)) {
                return published
            }

            if (published.latestVersionId) {
                def existingDrafts = getEntityClass(published).findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.DRAFT, [sort: 'versionNumber', order: 'desc'])
                for (existing in existingDrafts) {
                    if (existing.id != published.id) {
                        return existing
                    }
                }
            }
        }

        // only the first element in the chain can be forced
        context.stopForcingNew()

        startUpdating()

        for (CatalogueElement element in required) {
            if (!isDraft(element)) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (context.dataModel && context.dataModel != element.dataModel && context.dataModel != element) {
                    processed << element.id
                    continue
                }
                if (element.id in processed || isUpdatingInProgress(element) || isDeprecated(element)) {
                    continue
                }
                processed << element.id
                log.debug "Requesting draft creation of $element from $published"
                CatalogueElement draft = element.createDraftVersion(publisher, context)
                if (draft.hasErrors()) {
                    String message = FriendlyErrors.printErrors("Draft version $draft has errors", draft.errors)
                    log.warn(message)
                    return rejectDraftDependency(draft, message)
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

        Class<? extends CatalogueElement> type = context.newType ?: getEntityClass(published)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass

        CatalogueElement draft = type.newInstance()

        draft.dataModel = context.getDestinationDataModel(published)

        for (prop in domainClass.persistentProperties) {
            if (!prop.association && published.hasProperty(prop.name) && prop.name != 'dataModel') {
                draft.setProperty(prop.name, published.getProperty(prop.name))
            }
        }

        draft.versionNumber = (CatalogueElement.findByLatestVersionId(published.latestVersionId, [sort: 'versionNumber', order: 'desc'])?.versionNumber ?: 1) + 1
        draft.versionCreated = new Date()

        draft.latestVersionId = published.latestVersionId ?: published.id
        draft.status = ElementStatus.UPDATED
        draft.dateCreated = published.dateCreated

        draft.beforeDraftPersisted()

        if (!draft.save(flush: true, deepValidate: false)) {
            return draft
        }

        restoreStatus()


        draft.addToSupersedes(published, skipUniqueChecking: true)

        context.delayRelationshipCopying(draft, published)

        published.afterDraftPersisted(draft)

        if (published.status == ElementStatus.DRAFT) {
            archiver.archive(published, true)
        }

        draft.status = ElementStatus.DRAFT
        draft.save(flush: true, deepValidate: false)
    }


    private CatalogueElement rejectDraftDependency(CatalogueElement element, String message) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.cannot.create.draft.dependency', "Cannot create draft of dependency ${element}, please, resolve the issue first. You'll see more details when you try to create draft manualy\n\n$message")
        published
    }

    private CatalogueElement rejectRequiredDependency(CatalogueElement element) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.required.draft.dependency', "Dependency ${element} is not draft. Please, create draft for it first.")
        published
    }

}
