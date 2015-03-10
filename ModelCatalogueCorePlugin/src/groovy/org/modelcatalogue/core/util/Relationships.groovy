package org.modelcatalogue.core.util

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships implements ListWrapper<Relationship>, HasListWrapper<Relationship> {

    CatalogueElement owner
    RelationshipDirection direction
    RelationshipType type

    @Delegate ListWrapper<Relationship> list

    Class<Relationship> getItemType() { Relationship }

}
