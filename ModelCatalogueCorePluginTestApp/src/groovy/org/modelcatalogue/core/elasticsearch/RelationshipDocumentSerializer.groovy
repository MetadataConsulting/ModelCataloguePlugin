package org.modelcatalogue.core.elasticsearch

import static org.modelcatalogue.core.elasticsearch.CatalogueElementDocumentSerializer.safePut

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.Relationship

class RelationshipDocumentSerializer implements DocumentSerializer<Relationship> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, Relationship relationship, ImmutableMap.Builder<String, Object> builder) {
        safePut(builder, 'incoming_index', relationship.incomingIndex)
        safePut(builder, 'outgoing_index', relationship.outgoingIndex)
        safePut(builder, 'relationship_type', session.getDocument(relationship.relationshipType).payload)
        safePut(builder, 'source', session.getDocument(relationship.source).payload)
        safePut(builder, 'destination', session.getDocument(relationship.destination).payload)
        safePut(builder, 'archived', relationship.archived)
        safePut(builder, 'inherited', relationship.inherited)
        safePut(builder, 'ext', CatalogueElementDocumentSerializer.getExtensions(relationship.ext))
        return builder
    }
}
