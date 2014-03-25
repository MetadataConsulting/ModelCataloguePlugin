package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement

/**
 * Created by ladin on 14.02.14.
 */
abstract class CatalogueElementMarshallers extends AbstractMarshallers {

    CatalogueElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                outgoingRelationships: [count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"],
                incomingRelationships: [count: el.incomingRelationships ? el.incomingRelationships.size() : 0, itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"]
        ]

        def relationships   = GrailsClassUtils.getStaticFieldValue(type, 'relationships')   ?: [:]

        relationships.incoming?.each addRelationsJson('incoming', el, ret)
        relationships.outgoing?.each addRelationsJson('outgoing', el, ret)

        ret

    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
            outgoingRelationships count: el.outgoingRelationships ? el.outgoingRelationships.size() : 0, itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"
            incomingRelationships count: el.incomingRelationships ? el.incomingRelationships.size() : 0, itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"
        }

        def relationships   = GrailsClassUtils.getStaticFieldValue(type, 'relationships')   ?: [:]

        relationships.incoming?.each addRelationsXml('incoming', el, xml)
        relationships.outgoing?.each addRelationsXml('outgoing', el, xml)
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(GrailsNameUtils.getNaturalName(el.class.simpleName), "elementTypeName", xml)
    }

    private Closure addRelationsJson(String incomingOrOutgoing, CatalogueElement el, Map ret) {
        { String relationshipType, String name ->
            ret[name] = [count: el."count${name.capitalize()}"(), itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}"]
        }
    }

    private Closure addRelationsXml(String incomingOrOutgoing, CatalogueElement el, XML xml) {
        { String relationshipType, String name ->
            xml. build {
                "${name}" count: el."count${name.capitalize()}"(), itemType: 'relationship', link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}"
            }
        }
    }

}
