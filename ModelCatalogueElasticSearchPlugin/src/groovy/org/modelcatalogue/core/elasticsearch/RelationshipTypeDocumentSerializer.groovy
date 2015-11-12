package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.RelationshipType

class RelationshipTypeDocumentSerializer implements DocumentSerializer<RelationshipType> {

    Map getDocument(RelationshipType type) {
        [
                name: type.name,
                system: type.system,
                source_to_destination: type.sourceToDestination,
                source_to_destination_description: type.sourceToDestinationDescription,
                destination_to_source: type.destinationToSource,
                destination_to_source_description: type.destinationToSourceDescription,
                source_class: type.sourceClass.toString(),
                destination_class: type.destinationClass.toString(),
                bidirectional: type.bidirectional,
                version_specific: type.versionSpecific
        ]

    }
}
