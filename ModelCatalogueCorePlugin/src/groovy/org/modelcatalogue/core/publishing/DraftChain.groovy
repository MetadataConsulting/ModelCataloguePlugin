package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus

class DraftChain extends PublishingChain {


    private DraftChain(CatalogueElement published) {
        super(published)
    }

    static DraftChain create(CatalogueElement published) {
        return new DraftChain(published)
    }

    CatalogueElement run(Publisher<CatalogueElement> publisher) {
        if (isDraft(published)) {
            return published
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
                CatalogueElement draft = element.createDraftVersion(publisher)
                if (draft.hasErrors()) {
                    return rejectDraftDependency(draft)
                }
            }
        }
        return doPublish(publisher)
    }

    private CatalogueElement doPublish(Publisher<CatalogueElement> archiver) {
        published.status = ElementStatus.DRAFT

        if (published.latestVersionId) {
            List<CatalogueElement> previousDraft = published.getClass().findAllByLatestVersionIdAndStatus(published.latestVersionId, ElementStatus.DRAFT)
            for (CatalogueElement e in previousDraft) {
                if (e != published) {
                    archiver.archive(e)
                }
            }
        }

        published.save()
        published
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
