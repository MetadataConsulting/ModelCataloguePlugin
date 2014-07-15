package org.modelcatalogue.core.util

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships implements ListWrapper<Relationship> {

    CatalogueElement owner
    RelationshipDirection direction

    @Delegate ListWrapper<Relationship> list

    Class<Relationship> getItemType() { Relationship }

}
