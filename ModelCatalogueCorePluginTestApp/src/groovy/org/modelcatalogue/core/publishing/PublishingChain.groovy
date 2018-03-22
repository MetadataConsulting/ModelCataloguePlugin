package org.modelcatalogue.core.publishing

import groovy.util.logging.Log4j
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.builder.ProgressMonitor
import rx.Observer

@Log4j
abstract class PublishingChain {

    protected final CatalogueElement published

    // using collection of collection because some of them could be lazy collections
    protected final Collection<Collection<CatalogueElement>> queue = []
    protected final Collection<CatalogueElement> required = []
    protected final Set<Long> processed = []

    protected ElementStatus initialStatus

    public static PublishingChain createFinalizationChain(DataModel published) {
        FinalizationChain.create(published)
    }

    public static PublishingChain createFinalizationChain(CatalogueElement published) {
        if (HibernateProxyHelper.getClassWithoutInitializingProxy(published) == DataModel) {
            return FinalizationChain.create(published as DataModel)
        }
        LegacyFinalizationChain.create(published)
    }

    public static PublishingChain createDraftChain(DataModel published, DraftContext strategy) {
        return DraftChain.create(published as DataModel, strategy)
    }

    public static PublishingChain createCloneChain(CatalogueElement toBeCloned, CloningContext context) {
        CloningChain.create(toBeCloned, context)
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

    final CatalogueElement run(Publisher<CatalogueElement> publisher, Observer<String> monitor = ProgressMonitor.NOOP) {
        try {
            CatalogueElement result = doRun(publisher, monitor)
            monitor.onCompleted()
            return result
        } catch (Exception e) {
            log.error("Error processing chain", e)
            monitor.onError(e)
            published.errors.reject('publishing.error', e.toString())
            return published
        }
    }

    protected abstract CatalogueElement doRun(Publisher<CatalogueElement> publisher, Observer<String> monitor)

    protected void restoreStatus() {
        if (published.status != initialStatus) {
            published.status = initialStatus

            if (published.hasErrors()) {
                log.error FriendlyErrors.printErrors("Errors while creating drafts for $published:", published.errors)
                published.clearErrors()
            }
            try {
                FriendlyErrors.failFriendlySave(published)
            } catch (e) {
                log.error("Error restoring $published state", e)
            }
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

    protected static boolean isDraft(CatalogueElement element) {
        element.status == ElementStatus.DRAFT
    }

    protected static boolean isDeprecated(CatalogueElement element) {
        element.status == ElementStatus.DEPRECATED
    }

}
