package org.modelcatalogue.core.util

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

/**
 * A DataModelFilter uses dataModels as filters for content. To get the idea, remember that there was a time when DataModels were "Classifiers" of DataClasses which were "Models".
 *
 * DataModelFilter then acts as a "classifier" of content itself, by having DataModels which are included and those which are excluded.
 *
 * includes: Set of ids of DataModels which are to be included in some operation.
 * excludes: Set of ids of DataModels which are to excluded in some operation.
 *
 * The precise semantics is really up to the user of the filter.
 * This class itself imposes no opinion about how the included/excluded sets should be combined.
 * (e.g. 1. use every DataModel in the included set, except the excluded set, i.e. "includes - excludes"
 * or use 2. ((any DataModel NOT in the excluded set) + (any DataModel in the included set)) i.e. "all data models - (excludes - includes)"
 * or 3. only accept one of the sets to be non-empty, and use the set "includes" to specify itself, and "excludes" to specify "all data models - excludes"
 * )
 * For example, in topLevelDataClass methods, we have said you can only have one or the other set: includes or excludes, and the use of excludes is to specify "all data models - excludes".
 * In the content method of DataClassController, both sets are used, so that option 1 is specified: anything (NOT in excluded) AND (in included)
 *

 */
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


    DataModelFilter withImports(List<DataModel> subscribedModels) {

        if (includesImports || unclassifiedOnly) {
            return this
        }
        ImmutableSet.Builder<Long> includes = ImmutableSet.builder().addAll(includes)

        for (DataModel model in this.includes.collect { Long id -> DataModel.get(id) }) {
            includes.add(model.getId())
            for (Relationship imported in model.importsRelationships) {
                if(subscribedModels.find{it.id == imported.getDestination().getId()}) includes.add(imported.getDestination().getId())
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
