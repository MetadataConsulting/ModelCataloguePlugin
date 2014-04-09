package org.modelcatalogue.core.util

import org.modelcatalogue.core.Relationship

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships extends ListWrapper<Relationship> {
    String direction

    Relationships() {
        itemType = Relationship
    }
}
