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
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                sourceToDestination: el.sourceToDestination,
                destinationToSource: el.destinationToSource,
                sourceClass: el.sourceClass,
                destinationClass: el.destinationClass,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
        ]
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            sourceToDestination el.sourceToDestination
            destinationToSource el.destinationToSource
            sourceClass el.sourceClass
            destinationClass el.destinationClass
                   }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(GrailsNameUtils.getNaturalName(el.class.simpleName), "elementTypeName", xml)
    }

}




