package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

@Log4j
class DraftChain extends PublishingChain {

    private final DraftContext context

    private DraftChain(DataModel published, DraftContext context) {
        super(published)
        this.context = context
    }

    private DataModel getPublishedDataModel() {
        return published as DataModel
    }

    static DraftChain create(DataModel published, DraftContext strategy) {
        return new DraftChain(published, strategy)
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        if (!context.forceNew) {
            if (isDraft(published)) {
                published.clearErrors()
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

        log.debug("Creating draft for $published ($context) ...")

        DataModel draftDataModel = createDraft(publishedDataModel, null, publisher)

        for (CatalogueElement element in publishedDataModel.declares) {
            createDraft(element, draftDataModel, publisher)
        }

        return draftDataModel
    }

    private <T extends CatalogueElement> T createDraft(T element, DataModel draftDataModel, Publisher<CatalogueElement> archiver) {
        if (!element.latestVersionId) {
            element.latestVersionId = element.id
            FriendlyErrors.failFriendlySave(element)
        }

        Class<? extends CatalogueElement> type = context.getNewType(element) ?: getEntityClass(element)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass

        CatalogueElement draft = type.newInstance()

        draft.dataModel = draftDataModel

        for (prop in domainClass.persistentProperties) {
            if (!prop.association && element.hasProperty(prop.name) && prop.name != 'dataModel') {
                draft.setProperty(prop.name, element.getProperty(prop.name))
            }
        }

        draft.versionNumber = element.countVersions() + 1
        draft.versionCreated = new Date()

        draft.latestVersionId = element.latestVersionId ?: element.id
        draft.status = ElementStatus.UPDATED
        draft.dateCreated = element.dateCreated

        if (draft.instanceOf(DataModel)) {
            if (context.hasVersion()) {
                draft.semanticVersion = context.version
            } else {
                String nextVersion = PublishingContext.nextPatchVersion(draft.semanticVersion)
                draft.semanticVersion = nextVersion
                context.version(nextVersion)
            }
            draft.revisionNotes = null
        }

        draft.beforeDraftPersisted(context)

        if (!draft.save(/*flush: true, */ deepValidate: false)) {
            return draft as T
        }

        draft.addToSupersedes(element, skipUniqueChecking: true)

        context.delayRelationshipCopying(draft, element)

        if (element.status == ElementStatus.DRAFT) {
            archiver.archive(element, true)
        }

        draft.status = ElementStatus.DRAFT
        draft.save(/*flush: true, */ deepValidate: false)

        context.addResolution(element, draft)

        log.debug("... created draft for $element ($context)")

        return draft as T
    }

}
