package org.modelcatalogue.core.util

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers

/**
 * Created by ladin on 23.03.15.
 */
class ClassificationFilter {

    public static final ClassificationFilter NO_FILTER = new ClassificationFilter(false)
    public static final String UNCLASSIFIED_ONLY_KEY = '$unclassifiedOnly'
    public static final String EXCLUDE_KEY = '$exclude'

    private ClassificationFilter(ImmutableSet<Classification> includes, ImmutableSet<Classification> excludes) {
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
        new ClassificationFilter(ImmutableSet.copyOf(classifications), ImmutableSet.of())
    }

    static ClassificationFilter excludes(Iterable<Classification> classifications) {
        new ClassificationFilter(ImmutableSet.of(), ImmutableSet.copyOf(classifications))
    }

    static ClassificationFilter create(boolean unclassifiedOnly) {
        new ClassificationFilter(unclassifiedOnly)
    }

    static ClassificationFilter create(Iterable<Classification> includes, Iterable<Classification> excludes) {
        new ClassificationFilter(ImmutableSet.copyOf(includes), ImmutableSet.copyOf(excludes))
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

        if (user.ext[UNCLASSIFIED_ONLY_KEY]) {
            return new ClassificationFilter(true)
        }

        if (!user.filteredByRelationships) {
            return NO_FILTER
        }

        ImmutableSet.Builder<Classification> includes = ImmutableSet.builder()
        ImmutableSet.Builder<Classification> excludes = ImmutableSet.builder()

        for (Relationship rel in user.filteredByRelationships) {
            if (rel.ext[EXCLUDE_KEY]) {
                excludes.add(rel.source)
            } else {
                includes.add(rel.source)
            }
        }
        return new ClassificationFilter(includes.build(), excludes.build())
    }

    final ImmutableSet<Classification> includes
    final ImmutableSet<Classification> excludes

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

        for (Classification classification in includes) {
            user.addToFilteredBy classification
        }

        for (Classification classification in excludes) {
            user.addToFilteredBy classification, metadata: [(EXCLUDE_KEY): 'true']
        }
    }

    Map<String, Object> toMap() {
        [
                unclassifiedOnly: unclassifiedOnly,
                includes: includes.collect { CatalogueElementMarshallers.minimalCatalogueElementJSON(it) },
                excludes: excludes.collect { CatalogueElementMarshallers.minimalCatalogueElementJSON(it) }
        ]
    }


}
