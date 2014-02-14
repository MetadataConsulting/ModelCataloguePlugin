package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import grails.util.GrailsNameUtils
import uk.co.mc.core.CatalogueElement

/**
 * Created by ladin on 14.02.14.
 */
class CatalogueElementMarshallers implements MarshallersProvider {


    void register() {
        JSON.registerObjectMarshaller(CatalogueElement) { CatalogueElement el ->
            prepareJsonMap(el)
        }
        XML.registerObjectMarshaller(CatalogueElement) { CatalogueElement el, XML xml ->
            buildXml(el, xml)
        }
    }


    static Map<String, Object> prepareJsonMap(CatalogueElement el) {
        [
                id: el.id,
                name: el.name,
                description: el.description,
                version: el.version,
                outgoingRelationships: [count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/outgoing/$el.id"],
                incomingRelationships: [count: el.incomingRelationships ? el.incomingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/incoming/$el.id"]

        ]
    }

    static void buildXml(CatalogueElement el, XML xml) {
        xml.attribute("id", "${el.id}")
        xml.attribute("version", "${el.version}")

        xml.build {
            name el.name
            description el.description
            outgoingRelationships count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/outgoing/$el.id"
            incomingRelationships count: el.incomingRelationships ? el.incomingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/incoming/$el.id"
        }
    }

}
