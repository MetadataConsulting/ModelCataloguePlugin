package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus

class FinalizationChain extends PublishingChain {


    private FinalizationChain(CatalogueElement published) {
        super(published)
    }

    static FinalizationChain create(CatalogueElement published) {
        return new FinalizationChain(published)
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        if (published.published || isUpdatingInProgress(published)) {
            return published
        }

        if (published.status != ElementStatus.DRAFT) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.must.be.draft', 'Element is not draft!')
            return published
        }

        startUpdating()

        for (CatalogueElement element in required) {
            if (!element.published) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (element.id in processed || isUpdatingInProgress(element)) {
                    continue
                }
                processed << element.id
                CatalogueElement finalized = element.publish(publisher)
                if (finalized.hasErrors()) {
                    return rejectFinalizationDependency(finalized)
                }
            }
        }
        return doPublish(publisher)
    }

    private CatalogueElement doPublish(Publisher<CatalogueElement> archiver) {
        published.status = ElementStatus.FINALIZED
        published.save(flush: true, deepValidate: false)

        if (published.latestVersionId) {
            List<CatalogueElement> previousFinalized = published.getClass().findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.FINALIZED)
            for (CatalogueElement e in previousFinalized) {
                if (e != published) {
                    archiver.archive(e)
                }
            }
        }

        published
    }


    private CatalogueElement rejectFinalizationDependency(CatalogueElement element) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.cannot.finalize.dependency', "Cannot finalize dependency ${element}, please, resolve the issue first. You'll see more details when you try to finalize it manualy")
        published
    }

    private CatalogueElement rejectRequiredDependency(CatalogueElement element) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.required.finalization.dependency', "Dependency ${element} is not finalized. Please, finalize it first.")
        published
    }

}
