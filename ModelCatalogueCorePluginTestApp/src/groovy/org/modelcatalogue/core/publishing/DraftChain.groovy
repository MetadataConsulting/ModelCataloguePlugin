package org.modelcatalogue.core.publishing

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.builder.ProgressMonitor
import rx.Observer

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

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher, Observer<String> monitor) {
        if (!context.forceNew) {
            if (isDraft(published)) {
                published.clearErrors()
                monitor.onNext("\nData model is already ${published.status}")
                return published
            }

            if (published.latestVersionId) {
                def existingDrafts = getEntityClass(published).findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.DRAFT, [sort: 'versionNumber', order: 'desc'])
                for (existing in existingDrafts) {
                    if (existing.id != published.id) {
                        monitor.onNext("\nExisting draft data model available: <a class='new-version-link' href='#/$existing.id/dataModel/$existing.id/'>$existing</a>")
                        return existing
                    }
                }
            }
        }

        long total = publishedDataModel.countDeclares()

        monitor.onNext("Creating draft [00001/${(total + 1).toString().padLeft(5,'0')}]: $publishedDataModel")
        DataModel draftDataModel = createDraft(publishedDataModel, null, publisher, monitor)
        monitor.onNext(" - Created draft [00001/${(total + 1).toString().padLeft(5,'0')}]: $publishedDataModel")


        publishedDataModel.declares.eachWithIndex { CatalogueElement element, int index ->
            monitor.onNext("Creating draft [${(index + 2).toString().padLeft(5,'0')}/${(total + 1).toString().padLeft(5,'0')}]: $element")
            createDraft(element, draftDataModel, publisher, monitor)
            monitor.onNext(" - Created draft [${(index + 2).toString().padLeft(5,'0')}/${(total + 1).toString().padLeft(5,'0')}]: $element")
        }


        if (!draftDataModel.hasErrors()) {
            context.resolvePendingRelationships(monitor)
        }

        monitor.onNext("\nDraft data model will be available at <a class='new-version-link' href='#/$draftDataModel.id/dataModel/$draftDataModel.id/'>$draftDataModel</a> in couple of seconds")

        return draftDataModel
    }

    public <T extends CatalogueElement> T changeType(T element, Publisher<CatalogueElement> archiver) {
        return createDraft(element, element.dataModel, archiver, ProgressMonitor.NOOP)
    }

    private <T extends CatalogueElement> T createDraft(T element, DataModel draftDataModel, Publisher<CatalogueElement> archiver, Observer<String> monitor) {
        if (!element.latestVersionId) {
            element.latestVersionId = element.id
            FriendlyErrors.failFriendlySave(element)
        }

        Class<? extends CatalogueElement> type = context.getNewType(element) ?: getEntityClass(element)


        CatalogueElement draft = type.newInstance()

        draft.dataModel = draftDataModel


        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass
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
            Throwable error = new IllegalStateException(FriendlyErrors.printErrors("Failed to create draft", draft.errors))
            monitor.onError(error)
            throw error
        }

        draft.addToSupersedes(element, skipUniqueChecking: true)

        context.delayRelationshipCopying(draft, element)

        draft.status = element.status == ElementStatus.FINALIZED ? ElementStatus.DRAFT : element.status

        if (element.status == ElementStatus.DRAFT) {
            archiver.archive(element, true)
        }

        draft.save(/*flush: true, */ deepValidate: false)

        context.addResolution(element, draft)



        return draft as T
    }

}
