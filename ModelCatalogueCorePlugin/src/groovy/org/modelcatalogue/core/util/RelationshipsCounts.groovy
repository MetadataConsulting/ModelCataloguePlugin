package org.modelcatalogue.core.util

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType

class RelationshipsCounts implements Serializable {

    static final RelationshipsCounts EMPTY = create(ImmutableMap.of(), ImmutableMap.of())

    private final ImmutableMap<Long, Integer> incomingCounts
    private final ImmutableMap<Long, Integer> outgoingCounts


    static <T extends CatalogueElement> RelationshipsCounts create(ImmutableMap incomingCounts, ImmutableMap outgoingCounts) {
        return new RelationshipsCounts(incomingCounts, outgoingCounts)
    }

    private RelationshipsCounts(ImmutableMap<Long, Integer> incomingCounts, ImmutableMap<Long, Integer> outgoingCounts) {
        this.incomingCounts = incomingCounts
        this.outgoingCounts = outgoingCounts
    }

    int count(RelationshipDirection direction, RelationshipType type) {
        switch (direction) {
            case RelationshipDirection.INCOMING:
                return incomingCounts.get(type.getId()) ?: 0
            case RelationshipDirection.OUTGOING:
                return incomingCounts.get(type.getId()) ?: 0
            case RelationshipDirection.BOTH:
                return count(RelationshipDirection.INCOMING, type) + count(RelationshipDirection.OUTGOING, type)
        }
    }


}
