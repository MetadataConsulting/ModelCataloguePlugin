package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

class RelationshipDocumentSerializer implements DocumentSerializer<Relationship> {

    Map getDocument(Relationship relationship) {
        [
                incoming_index: relationship.incomingIndex,
                outgoing_index: relationship.outgoingIndex,
                relationship_type: DocumentSerializer.Registry.get(RelationshipType).getDocument(relationship.relationshipType),
                source: DocumentSerializer.Registry.get(relationship.source.class).getDocument(relationship.source),
                destination: DocumentSerializer.Registry.get(relationship.destination.class).getDocument(relationship.destination),
                archived: relationship.archived,
                inherited: relationship.inherited,
                ext: relationship.ext.collect { key, value -> [key: key, value: value] }
        ]
    }

}
