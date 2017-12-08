package org.modelcatalogue.core.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import com.google.common.cache.RemovalNotification
import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.RelationshipsCounts
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import rx.subjects.Subject
import java.util.concurrent.TimeUnit

/**
 * New service which should be responsible for caching functionality. It is a successor of cache implementation around
 * the app.
 */
@Transactional
class CacheService {

    static final Cache<Long, Integer> VERSION_COUNT_CACHE = CacheBuilder.newBuilder().initialCapacity(1000).build()
    static final Cache<Class, List<Class>> SUBCLASSES_CACHE = CacheBuilder.newBuilder().initialCapacity(20).build()
    static final Cache<Long, Set<Long>> FAVORITE_CACHE = CacheBuilder.newBuilder().initialCapacity(20).build()
    static final Cache<Long, RelationshipsCounts> RELATIONSHIPS_COUNT_CACHE = CacheBuilder.newBuilder().initialCapacity(1000).build()
    static final Cache<String, Long> TYPES_CACHE = CacheBuilder.newBuilder().initialCapacity(20).build()
    static final Cache<Long, DataModelFilter> FILTERS_CACHE = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS).build()
    static final Cache<String, BuildProgressMonitor> MONITORS_CACHE = CacheBuilder.newBuilder().initialCapacity(20).expireAfterAccess(1, TimeUnit.DAYS).build()
    static Cache<String, Map<String, Map>> MAPPINGS_CACHE = CacheBuilder.newBuilder().initialCapacity(20).build()

    static final Cache<String, Subject<Map<String, Object>, Map<String, Object>>> DEBOUNCE_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Subject<Map<String, Object>, Map<String, Object>>>() {
                @Override
                void onRemoval(RemovalNotification<String, Subject<Map<String, Object>, Map<String, Object>>> notification) {
                    notification.value.onCompleted()
                }
            })
            .build()

    def elementService
    def relationshipService

    void clearCache() {
        [VERSION_COUNT_CACHE, SUBCLASSES_CACHE, FAVORITE_CACHE, RELATIONSHIPS_COUNT_CACHE, TYPES_CACHE, MAPPINGS_CACHE, MONITORS_CACHE, FILTERS_CACHE, DEBOUNCE_CACHE].each {
            it.invalidateAll()
            it.cleanUp()
        }
    }

    /**
     * Invalidate {@link CatalogueElement} from all caches.
     * @param catalogueElement Catalogue element to be invalidated.
     */
    void invalidate(Long id, Long latestVersionId) {
        log.debug("invalidating cache for $id ($latestVersionId)")
        VERSION_COUNT_CACHE.invalidate(latestVersionId)
        VERSION_COUNT_CACHE.cleanUp()
        FAVORITE_CACHE.invalidate(id)
        FAVORITE_CACHE.cleanUp()
        RELATIONSHIPS_COUNT_CACHE.invalidate(id)
        RELATIONSHIPS_COUNT_CACHE.cleanUp()
    }
}
