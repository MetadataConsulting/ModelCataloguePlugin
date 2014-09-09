package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.RelationshipType

class RelationshipTypeMarshaller extends AbstractMarshallers {

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
                rule: el.rule,
                bidirectional: el.bidirectional,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                metadataHints: el.metadataHints?.split(/\s*,\s*/)?.grep() ?: []
        ]
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            sourceToDestination el.sourceToDestination
            destinationToSource el.destinationToSource
            sourceClass el.sourceClass
            destinationClass el.destinationClass
            rule el.rule
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(el.metadataHints, "metadataHints", xml)
        addXmlAttribute(el.bidirectional, "bidirectional", xml)
        addXmlAttribute(el.system, "system", xml)
    }

}




