package org.modelcatalogue.core.util.lists

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.HasListWrapper
import org.modelcatalogue.core.util.lists.ListWrapper

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
