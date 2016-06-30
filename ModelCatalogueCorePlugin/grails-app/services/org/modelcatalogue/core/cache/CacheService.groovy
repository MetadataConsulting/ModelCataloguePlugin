package org.modelcatalogue.core.cache

import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement

/**
 * New service which should be responsible for caching functionality. It is a successor of cache implementation around
 * the app.
 */
@Transactional
class CacheService {

    def elementService
    def relationshipService

    /**
     * Invalidate {@link CatalogueElement} from all caches.
     * @param catalogueElement Catalogue element to be invalidated.
     */
    void invalidate(CatalogueElement catalogueElement) {
        log.debug("invalidating cache for $catalogueElement")
        elementService.invalidateCache(catalogueElement)
        relationshipService.invalidateCache(catalogueElement)
    }
}
