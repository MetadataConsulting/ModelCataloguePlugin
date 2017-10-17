package org.modelcatalogue.core.publishing

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.HibernateHelper
import rx.Observer

@Log4j
class FinalizationChain extends PublishingChain {


    private FinalizationChain(DataModel published) {
        super(published)
    }

    static FinalizationChain create(DataModel published) {
        return new FinalizationChain(published)
    }

    private DataModel getPublishedDataModel() {
        return published as DataModel
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher, Observer<String> monitor) {
        if (published.published) {
            monitor.onNext("Already published")
            return published
        }

        if (published.status != ElementStatus.DRAFT) {
            final String message = 'Element is not draft!'
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.must.be.draft', message)
            monitor.onNext(message)
            return published
        }

        published.status = ElementStatus.PENDING
        published.merge()
        published.save()

        for (CatalogueElement element in publishedDataModel.declares) {
            for (CatalogueElement dependency in element.collectExternalDependencies()) {
                if (dependency && dependency.status != ElementStatus.FINALIZED && dependency.status != ElementStatus.DEPRECATED) {
                    dependency = HibernateHelper.ensureNoProxy(dependency)
                    element = HibernateHelper.ensureNoProxy(element)
                    final String message = "Dependencies outside the current data model ${HibernateHelper.ensureNoProxy(published.dataModel)} must be finalized: $element => $dependency"
                    monitor.onNext(message)
                    published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.dependency.not.finalized', message)
                }
            }
        }

        if (published.hasErrors()) {
            monitor.onNext(FriendlyErrors.printErrors('Elements to be finalized not valid', published.errors))
            return published
        }

        for (CatalogueElement element in publishedDataModel.declares) {
            // already finalized for some reason (legacy data, unsuccessful previous finalization, deprecated)
            if (element.published) {
                continue
            }

            doPublish(element, publisher, monitor)
        }

        doPublish(published, publisher, monitor, true)
        monitor.onNext("Finalization finished")
        return published
    }

    private static CatalogueElement doPublish(CatalogueElement published, Publisher<CatalogueElement> archiver, Observer<String> monitor, boolean flush = false) {
        published = HibernateHelper.ensureNoProxy(published)
        monitor.onNext("Finalizing $published ...")

        published.status = ElementStatus.FINALIZED
        published.save(flush: flush, deepValidate: false)

        if (published.latestVersionId) {
            List<CatalogueElement> previousFinalized = HibernateHelper.getEntityClass(published).findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.FINALIZED)
            for (CatalogueElement e in previousFinalized) {
                if (e != published) {
                    archiver.archive(e, true)
                }
            }
        }

        monitor.onNext("... finalized $published")

        published
    }

}
