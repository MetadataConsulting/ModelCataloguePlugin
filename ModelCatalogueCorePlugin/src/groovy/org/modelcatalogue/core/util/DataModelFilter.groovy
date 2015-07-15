package org.modelcatalogue.core.util

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

import java.util.concurrent.TimeUnit

class DataModelFilter {

    public static final DataModelFilter NO_FILTER = new DataModelFilter(false)

    private static final Cache<Long, DataModelFilter> filtersCache = CacheBuilder
            .newBuilder()
            .initialCapacity(10)
            .maximumSize(100)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build()

    private static final String UNCLASSIFIED_ONLY_KEY = '$unclassifiedOnly'
    private static final String EXCLUDE_KEY = '$exclude'

    private DataModelFilter(ImmutableSet<Long> includes, ImmutableSet<Long> excludes) {
        this.includes = includes
        this.excludes = excludes
        this.unclassifiedOnly = false
    }

    private DataModelFilter(boolean unclassifiedOnly) {
        this.unclassifiedOnly = unclassifiedOnly
        this.includes = ImmutableSet.of()
        this.excludes = ImmutableSet.of()
    }

    static DataModelFilter includes(Iterable<DataModel> dataModels) {
        new DataModelFilter(ImmutableSet.copyOf(dataModels.collect { it.id }), ImmutableSet.of())
    }

    static DataModelFilter excludes(Iterable<DataModel> dataModels) {
        new DataModelFilter(ImmutableSet.of(), ImmutableSet.copyOf(dataModels.collect { it.id }))
    }

    static DataModelFilter create(boolean orphanedOnly) {
        new DataModelFilter(orphanedOnly)
    }

    static DataModelFilter create(Iterable<DataModel> includes, Iterable<DataModel> excludes) {
        new DataModelFilter(ImmutableSet.copyOf(includes.collect { it.id }), ImmutableSet.copyOf(excludes.collect { it.id }))
    }

    static DataModelFilter includes(DataModel... includes) {
        new DataModelFilter(ImmutableSet.copyOf(includes.collect { it.id }), ImmutableSet.of())
    }

    static DataModelFilter excludes(DataModel... excludes) {
        new DataModelFilter(ImmutableSet.of(), ImmutableSet.copyOf(excludes.collect { it.id }))
    }

    static DataModelFilter from(Map<String, Object> json) {
        if (json.unclassifiedOnly) {
            return create(true)
        }
        return create((json.includes ?: []).collect { DataModel.get(it.id) }, (json.excludes ?: []).collect { DataModel.get(it.id) })
    }

    static DataModelFilter from(Object other) {
        return NO_FILTER
    }

    static DataModelFilter from(User user) {
        if (!user) {
            return NO_FILTER
        }

        filtersCache.get(user.getId()) {
            if (user.ext[UNCLASSIFIED_ONLY_KEY]) {
                return new DataModelFilter(true)
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
            return new DataModelFilter(includes.build(), excludes.build())
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

        user.filteredBy.each { DataModel c ->
            user.removeFromFilteredBy(c)
        }

        for (Long id in includes) {
            user.addToFilteredBy DataModel.get(id)
        }

        for (Long id in excludes) {
            user.addToFilteredBy DataModel.get(id), metadata: [(EXCLUDE_KEY): 'true']
        }

        filtersCache.put(user.getId(), this)
    }

    Map<String, Object> toMap() {
        [
                unclassifiedOnly: unclassifiedOnly,
                includes: includes.collect { CatalogueElementMarshaller.minimalCatalogueElementJSON(DataModel.get(it)) },
                excludes: excludes.collect { CatalogueElementMarshaller.minimalCatalogueElementJSON(DataModel.get(it)) }
        ]
    }


}
