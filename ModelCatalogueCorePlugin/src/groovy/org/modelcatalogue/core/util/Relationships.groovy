package org.modelcatalogue.core.util

import org.modelcatalogue.core.Relationship

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships extends ListWrapper {
    String direction

    Relationships() {
        itemType = Relationship.name
    }
}
