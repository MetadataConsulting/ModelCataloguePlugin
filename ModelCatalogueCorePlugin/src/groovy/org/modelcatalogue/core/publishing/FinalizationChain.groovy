package org.modelcatalogue.core.publishing

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.HibernateHelper
import org.springframework.validation.ObjectError

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

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        if (published.published || isUpdatingInProgress(published)) {
            return published
        }

        if (published.status != ElementStatus.DRAFT) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.must.be.draft', 'Element is not draft!')
            return published
        }

        for (CatalogueElement element in publishedDataModel.declares) {
            for (CatalogueElement dependency in element.collectExternalDependencies()) {
                published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.dependency.not.finalized', "Dependencies outside the current data model $published.dataModel must be finalized: $element => $dependency")
            }
        }

        if (published.hasErrors()) {
            return published
        }

        for (CatalogueElement element in publishedDataModel.declares) {
            // already finalized for some reason (legacy data, unsuccessful previous finalization, deprecated)
            if (element.published) {
                continue
            }

            doPublish(element, publisher)
        }

        return doPublish(published, publisher, true)
    }

    private static CatalogueElement doPublish(CatalogueElement published, Publisher<CatalogueElement> archiver, boolean flush = false) {
        log.debug("Finalizing $published ...")

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

        log.debug("... finalized $published")

        published
    }

}
