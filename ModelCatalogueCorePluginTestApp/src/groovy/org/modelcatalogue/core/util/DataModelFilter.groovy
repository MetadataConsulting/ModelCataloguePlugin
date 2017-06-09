package org.modelcatalogue.core.util

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

class DataModelFilter {

    public static final DataModelFilter NO_FILTER = new DataModelFilter(false)


    private static final String UNCLASSIFIED_ONLY_KEY = '$unclassifiedOnly'
    private static final String EXCLUDE_KEY = '$exclude'

    private DataModelFilter(ImmutableSet<Long> includes, ImmutableSet<Long> excludes, boolean includesImports) {
        this.includes = includes
        this.excludes = excludes
        this.includesImports = includesImports
        this.unclassifiedOnly = false
    }

    private DataModelFilter(boolean unclassifiedOnly) {
        /** Unclassified means those CatalogueElements with no data model.
         * Even though CatalogueElements should in principle have
         * a data model. For example, DataModels themselves do not have DataModels
         * they are part of, and they are not part of themselves.*/
        this.unclassifiedOnly = unclassifiedOnly
        this.includes = ImmutableSet.of()
        this.excludes = ImmutableSet.of()
        this.includesImports = true
    }

    static DataModelFilter includes(Iterable<DataModel> dataModels) {
        new DataModelFilter(ImmutableSet.copyOf(dataModels.collect { it.id }), ImmutableSet.of(), false)
    }

    static DataModelFilter excludes(Iterable<DataModel> dataModels) {
        new DataModelFilter(ImmutableSet.of(), ImmutableSet.copyOf(dataModels.collect { it.id }), true)
    }

    static DataModelFilter create(boolean orphanedOnly) {
        new DataModelFilter(orphanedOnly)
    }

    static DataModelFilter create(Iterable<DataModel> includes, Iterable<DataModel> excludes) {
        new DataModelFilter(ImmutableSet.copyOf(includes.collect { it.id }), ImmutableSet.copyOf(excludes.collect {
            it.id
        }), false)
    }

    static DataModelFilter includes(DataModel... includes) {
        new DataModelFilter(ImmutableSet.copyOf(includes.collect { it.id }), ImmutableSet.of(), false)
    }

    static DataModelFilter excludes(DataModel... excludes) {
        new DataModelFilter(ImmutableSet.of(), ImmutableSet.copyOf(excludes.collect { it.id }), true)
    }

    static DataModelFilter from(Map<String, Object> json) {
        if (json.unclassifiedOnly) {
            return create(true)
        }
        return create((json.includes ?: []).collect { DataModel.get(it.id) }, (json.excludes ?: []).collect {
            DataModel.get(it.id)
        })
    }

    static DataModelFilter from(Object other) {
        return NO_FILTER
    }

    static DataModelFilter from(User user) {
        if (!user) {
            return NO_FILTER
        }

        CacheService.FILTERS_CACHE.get(user.getId()) {
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
            return new DataModelFilter(includes.build(), excludes.build(), false)
        }
    }

    final ImmutableSet<Long> includes
    final ImmutableSet<Long> excludes

    final boolean unclassifiedOnly
    final boolean includesImports

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

        CacheService.FILTERS_CACHE.put(user.getId(), this)
    }

    Map<String, Object> toMap() {
        [
                unclassifiedOnly: unclassifiedOnly,
                includes        : includes.collect {
                    CatalogueElementMarshaller.minimalCatalogueElementJSON(DataModel.get(it))
                },
                excludes        : excludes.collect {
                    CatalogueElementMarshaller.minimalCatalogueElementJSON(DataModel.get(it))
                }
        ]
    }

    DataModelFilter withImports() {
        if (includesImports || unclassifiedOnly) {
            return this
        }
        ImmutableSet.Builder<Long> includes = ImmutableSet.builder().addAll(includes)

        for (DataModel model in this.includes.collect { Long id -> DataModel.get(id) }) {
            includes.add(model.getId())
            for (Relationship imported in model.importsRelationships) {
                includes.add(imported.getDestination().getId())
            }
        }

        return new DataModelFilter(includes.build(), excludes, true)
    }

    boolean isIncluding(DataModel model) {
        return model.getId() in includes
    }

    boolean isExcluding(DataModel model) {
        return model.getId() in excludes
    }

}
