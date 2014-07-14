package org.modelcatalogue.core.util

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships extends SimpleListWrapper<Relationship> {
    CatalogueElement owner
    RelationshipDirection direction

    Relationships() {
        itemType = Relationship
    }
}
