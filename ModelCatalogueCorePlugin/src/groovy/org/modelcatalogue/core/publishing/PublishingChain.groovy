package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors

abstract class PublishingChain {

    protected CatalogueElement published
    protected ElementStatus initialStatus

    // using collection of collection because some of them could be lazy collections
    protected Collection<Collection<CatalogueElement>> queue = []
    protected Collection<CatalogueElement> required = []
    protected Set<Long> processed = []


    public static PublishingChain finalize(CatalogueElement published) {
        FinalizationChain.create(published)
    }

    public static PublishingChain createDraft(CatalogueElement published, DraftContext strategy) {
        DraftChain.create(published, strategy)
    }

    protected PublishingChain(CatalogueElement published) {
        this.published = published
        this.initialStatus = published.status
    }

    /**
     * Require that the dependency must be already finalized.
     */
    PublishingChain require(CatalogueElement dependency) {
        if (!dependency) {
            return this
        }
        required << dependency
        this
    }

    PublishingChain add(Object object) {
        if (object instanceof Collection) {
            return add(object as Collection<CatalogueElement>)
        }
        if (!object) {
            return this
        }
        throw new IllegalArgumentException("Cannot publish ${object}")
    }

    PublishingChain add(CatalogueElement dependency) {
        if (!dependency) {
            return this
        }
        queue << [dependency]
        this
    }

    PublishingChain add(Collection<CatalogueElement> dependencies) {
        queue << dependencies
        this
    }

    abstract CatalogueElement run(Publisher<CatalogueElement> publisher)

    protected void restoreStatus() {
        if (published.status != initialStatus) {
            published.status = initialStatus
            published.clearErrors()
            FriendlyErrors.failFriendlySave(published)
        }
    }

    protected void startUpdating() {
        published.status = ElementStatus.UPDATED
        published.clearErrors()
        FriendlyErrors.failFriendlySave(published)
    }

    protected static boolean isUpdatingInProgress(CatalogueElement element) {
        element.status == ElementStatus.UPDATED
    }

}
