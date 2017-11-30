package org.modelcatalogue.core.elasticsearch

import static org.modelcatalogue.core.elasticsearch.CatalogueElementDocumentSerializer.safePut
import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.RelationshipType

class RelationshipTypeDocumentSerializer implements DocumentSerializer<RelationshipType> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, RelationshipType type, ImmutableMap.Builder<String, Object> builder) {
        safePut(builder, 'name', type.name)
        safePut(builder, 'system', type.system)
        safePut(builder, 'source_to_destination', type.sourceToDestination)
        safePut(builder, 'source_to_destination_description', type.sourceToDestinationDescription)
        safePut(builder, 'destination_to_source', type.destinationToSource)
        safePut(builder, 'destination_to_source_description', type.destinationToSourceDescription)
        safePut(builder, 'source_class', type.sourceClass.toString())
        safePut(builder, 'destination_class', type.destinationClass.toString())
        safePut(builder, 'bidirectional', type.bidirectional)
        safePut(builder, 'version_specific', type.versionSpecific)
        return builder
    }
}
