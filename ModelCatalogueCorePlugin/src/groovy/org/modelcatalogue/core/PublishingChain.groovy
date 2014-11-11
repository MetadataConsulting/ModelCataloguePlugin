package org.modelcatalogue.core

class PublishingChain {

    private CatalogueElement published
    private ElementStatus initialStatus

    // using collection of collection because some of them could be lazy collections
    private Collection<Collection<CatalogueElement>> queue = []
    private Collection<CatalogueElement> required = []
    private Set<Long> processed = []


    private PublishingChain(CatalogueElement published) {
        this.published = published
        this.initialStatus = published.status
    }

    static PublishingChain create(CatalogueElement published) {
        return new PublishingChain(published)
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

    PublishingChain publish(Object object) {
        if (object instanceof Collection) {
            return publish(object as Collection<CatalogueElement>)
        }
        if (!object) {
            return this
        }
        throw new IllegalArgumentException("Cannot publish ${object}")
    }

    PublishingChain publish(CatalogueElement dependency) {
        if (!dependency) {
            return this
        }
        queue << [dependency]
        this
    }

    PublishingChain publish(Collection<CatalogueElement> dependencies) {
        queue << dependencies
        this
    }

    CatalogueElement publish(Archiver<CatalogueElement> archiver) {
        if (published.published || isPublishingInProgress(published)) {
            return published
        }

        if (published.status != ElementStatus.DRAFT) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.must.be.draft', 'Element is not draft!')
            return published
        }

        startPublishing()

        for (CatalogueElement element in required) {
            if (!element.published) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (element.id in processed || isPublishingInProgress(element)) {
                    continue
                }
                processed << element.id
                CatalogueElement finalized = element.publish(archiver)
                if (finalized.hasErrors()) {
                    return rejectFinalizationDependency(finalized)
                }
            }
        }
        return doPublish(archiver)
    }

    private CatalogueElement doPublish(Archiver<CatalogueElement> archiver) {
        published.status = ElementStatus.FINALIZED

        if (published.latestVersionId) {
            List<CatalogueElement> previousFinalized = published.getClass().findAllByLatestVersionId(published.latestVersionId)
            for (CatalogueElement e in previousFinalized) {
                if (e != published) {
                    archiver.archive(e)
                }
            }
        }

        published.save()
        published
    }

    private void restoreStatus() {
        if (published.status != initialStatus) {
            published.status = initialStatus
            published.clearErrors()
            published.save(failOnError: true)
        }
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


    private void startPublishing() {
        published.status = ElementStatus.UPDATED
        published.clearErrors()
        published.save(failOnError: true)
    }

    private static boolean isPublishingInProgress(CatalogueElement element) {
        element.status == ElementStatus.UPDATED
    }

}
