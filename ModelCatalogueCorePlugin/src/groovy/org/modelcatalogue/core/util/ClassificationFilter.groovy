package org.modelcatalogue.core.util

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers

import java.util.concurrent.TimeUnit

class ClassificationFilter {

    public static final ClassificationFilter NO_FILTER = new ClassificationFilter(false)

    private static final Cache<Long, ClassificationFilter> filtersCache = CacheBuilder
            .newBuilder()
            .initialCapacity(10)
            .maximumSize(100)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build()

    private static final String UNCLASSIFIED_ONLY_KEY = '$unclassifiedOnly'
    private static final String EXCLUDE_KEY = '$exclude'

    private ClassificationFilter(ImmutableSet<Long> includes, ImmutableSet<Long> excludes) {
        this.includes = includes
        this.excludes = excludes
        this.unclassifiedOnly = false
    }

    private ClassificationFilter(boolean unclassifiedOnly) {
        this.unclassifiedOnly = unclassifiedOnly
        this.includes = ImmutableSet.of()
        this.excludes = ImmutableSet.of()
    }

    static ClassificationFilter includes(Iterable<Classification> classifications) {
        new ClassificationFilter(ImmutableSet.copyOf(classifications.collect { it.id }), ImmutableSet.of())
    }

    static ClassificationFilter excludes(Iterable<Classification> classifications) {
        new ClassificationFilter(ImmutableSet.of(), ImmutableSet.copyOf(classifications.collect { it.id }))
    }

    static ClassificationFilter create(boolean unclassifiedOnly) {
        new ClassificationFilter(unclassifiedOnly)
    }

    static ClassificationFilter create(Iterable<Classification> includes, Iterable<Classification> excludes) {
        new ClassificationFilter(ImmutableSet.copyOf(includes.collect { it.id }), ImmutableSet.copyOf(excludes.collect { it.id }))
    }

    static ClassificationFilter from(Map<String, Object> json) {
        if (json.unclassifiedOnly) {
            return create(true)
        }
        return create((json.includes ?: []).collect { Classification.get(it.id) }, (json.excludes ?: []).collect { Classification.get(it.id) })
    }

    static ClassificationFilter from(Object other) {
        return NO_FILTER
    }

    static ClassificationFilter from(User user) {
        if (!user) {
            return NO_FILTER
        }

        filtersCache.get(user.getId()) {
            if (user.ext[UNCLASSIFIED_ONLY_KEY]) {
                return new ClassificationFilter(true)
            }

            if (!user.filteredByRelationships) {
                return NO_FILTER
            }

            ImmutableSet.Builder<Long> includes = ImmutableSet.builder()
            ImmutableSet.Builder<Long> excludes = ImmutableSet.builder()

            for (Relationship rel in user.filteredByRelationships) {
                if (rel.ext[EXCLUDE_KEY]) {
                    excludes.add(rel.source.id)
                } else {
                    includes.add(rel.source.id)
                }
            }
            return new ClassificationFilter(includes.build(), excludes.build())
        }
    }

    final ImmutableSet<Long> includes
    final ImmutableSet<Long> excludes

    final boolean unclassifiedOnly

    /**
     * The value of the filter is truthy if the unclassifiedOnly flag is set of includes or excludes set is not empty.
     * @return true if the filter is truthy if the unclassifiedOnly flag is set of includes or excludes set is not empty.
     */
    boolean asBoolean() {
        unclassifiedOnly || !includes.empty || !excludes.empty
    }

    void to(User user) {
        if (unclassifiedOnly) {
            user.ext[UNCLASSIFIED_ONLY_KEY] = 'true'
        } else {
            user.ext.remove(UNCLASSIFIED_ONLY_KEY)
        }

        user.filteredBy.each { Classification c ->
            user.removeFromFilteredBy(c)
        }

        for (Long id in includes) {
            user.addToFilteredBy Classification.get(id)
        }

        for (Long id in excludes) {
            user.addToFilteredBy Classification.get(id), metadata: [(EXCLUDE_KEY): 'true']
        }

        filtersCache.put(user.getId(), this)
    }

    Map<String, Object> toMap() {
        [
                unclassifiedOnly: unclassifiedOnly,
                includes: includes.collect { CatalogueElementMarshallers.minimalCatalogueElementJSON(Classification.get(it)) },
                excludes: excludes.collect { CatalogueElementMarshallers.minimalCatalogueElementJSON(Classification.get(it)) }
        ]
    }


}
