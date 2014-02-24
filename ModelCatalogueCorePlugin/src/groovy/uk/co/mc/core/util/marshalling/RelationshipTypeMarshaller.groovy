package uk.co.mc.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import uk.co.mc.core.RelationshipType

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
                destinationClass: el.destinationClass
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
        xml.attribute("id", "${el.id}")
        xml.attribute("version", "${el.version}")
        xml.attribute("elementType", "${el.class.name}")
        xml.attribute("elementTypeName", "${GrailsNameUtils.getNaturalName(el.class.simpleName)}")
    }

}




