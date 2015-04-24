package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.RelationshipType

class RelationshipTypeMarshaller extends AbstractMarshaller {

    RelationshipTypeMarshaller() {
        super(RelationshipType)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        [
                id: el.id,
                name: el.name,
                version: el.version,
                elementType: el.class.name,
                sourceToDestination: el.sourceToDestination,
                destinationToSource: el.destinationToSource,
                sourceClass: el.sourceClass,
                destinationClass: el.destinationClass,
                system: el.system,
                versionSpecific: el.versionSpecific,
                sourceToDestinationDescription: el.sourceToDestinationDescription,
                destinationToSourceDescription: el.destinationToSourceDescription,
                rule: el.rule,
                bidirectional: el.bidirectional,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                metadataHints: el.metadataHints?.split(/\s*,\s*/)?.grep() ?: []
        ]
    }
}




