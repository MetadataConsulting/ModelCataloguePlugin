package uk.co.mc.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils

/**
 * Created by ladin on 14.02.14.
 */
abstract class CatalogueElementMarshallers extends AbstractMarshallers {

    CatalogueElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        [
                id: el.id,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                outgoingRelationships: [count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"],
                incomingRelationships: [count: el.incomingRelationships ? el.incomingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"]

        ]
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
            outgoingRelationships count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"
            incomingRelationships count: el.incomingRelationships ? el.incomingRelationships.size() : 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(GrailsNameUtils.getNaturalName(el.class.simpleName), "elementTypeName", xml)
    }

}
