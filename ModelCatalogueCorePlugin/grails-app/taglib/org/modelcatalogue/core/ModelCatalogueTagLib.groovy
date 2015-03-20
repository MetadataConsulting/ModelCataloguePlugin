package org.modelcatalogue.core

import org.modelcatalogue.core.util.RelationshipDirection

class ModelCatalogueTagLib {

    def relationshipService

    // static defaultEncodeAs = [taglib:'html']
    // static encodeAsForTags = [relationships: [taglib:'none']]

    static namespace = "mc"

    /**
     * Iterates over all relationships of given element having given type and direction.
     *
     * @attr element REQUIRED the element
     * @attr type name of the relationship type such as 'hierarchy', if not supplied it iterates over all the relationships
     * @attr direction direction of the relationship, one of 'outgoing', 'incoming' or 'bidirectional', defaults to 'bidirectional'
     * @attr var name of the variable for the relationship inside the tag, defaults to 'it'
     */
    def relationships = { attrs, body ->
        String var = attrs.var ?: 'it'
        if (!attrs.element) {
            throw new IllegalArgumentException('You must provide "element" attribute')
        }
        if (!(attrs.element instanceof CatalogueElement)) {
            throw new IllegalArgumentException('Attribute "element" must be CatalogueElement')
        }
        CatalogueElement element = attrs.element
        RelationshipType type = null
        if (attrs.type) {
            type = RelationshipType.readByName(attrs.type.toString())
            if (!type) {
                throw new IllegalArgumentException("Relationship type of given name \"${attrs.type}\" does not exist")
            }
        }
        RelationshipDirection direction = RelationshipDirection.BOTH
        if (attrs.direction) {
            switch (attrs.direction) {
                case 'outgoing':
                    direction = RelationshipDirection.OUTGOING
                    break
                case 'incoming':
                    direction = RelationshipDirection.INCOMING
                    break
                case 'bidirectional':
                    direction = RelationshipDirection.BOTH
                    break
                default:
                    throw new IllegalArgumentException("Direction 'incoming', 'outgoing' or 'bidirectional' expected but got '${attrs.direction}'")
            }
        }

        for (Relationship rel in relationshipService.getRelationships([:],direction, element, type).items) {
            out << body((var): rel) << '\n'
        }
    }
}
