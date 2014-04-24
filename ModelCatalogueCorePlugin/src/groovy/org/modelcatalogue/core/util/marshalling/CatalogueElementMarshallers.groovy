package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

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
                relationships: [count: (el.outgoingRelationships ? el.outgoingRelationships.size() : 0) + (el.incomingRelationships ? el.incomingRelationships.size() : 0), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"],
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(type)

        relationships.incoming?.each addRelationsJson('incoming', el, ret)
        relationships.outgoing?.each addRelationsJson('outgoing', el, ret)

        ret

    }

    static Map<String, Map<String, String>> getRelationshipConfiguration(Class type) {
        def relationships  = [incoming: [:], outgoing: [:]]
        if (type.superclass && CatalogueElement.isAssignableFrom(type.superclass)) {
            def fromSuperclass = getRelationshipConfiguration(type.superclass)
            relationships.incoming.putAll(fromSuperclass.incoming ?: [:])
            relationships.outgoing.putAll(fromSuperclass.outgoing ?: [:])
        }
        def fromType = GrailsClassUtils.getStaticFieldValue(type, 'relationships') ?: [incoming: [:], outgoing: [:]]
        relationships.incoming.putAll(fromType.incoming ?: [:])
        relationships.outgoing.putAll(fromType.outgoing ?: [:])
        relationships
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
            relationships count: (el.outgoingRelationships ? el.outgoingRelationships.size() : 0) + (el.incomingRelationships ? el.incomingRelationships.size() : 0), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"
        }

        def relationships = getRelationshipConfiguration(type)

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

    private static Closure addRelationsJson(String incomingOrOutgoing, CatalogueElement el, Map ret) {
        { String relationshipType, String name ->
            ret[name] = [count: el."count${name.capitalize()}"(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}"]
        }
    }

    private static Closure addRelationsXml(String incomingOrOutgoing, CatalogueElement el, XML xml) {
        { String relationshipType, String name ->
            xml. build {
                "${name}" count: el."count${name.capitalize()}"(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}"
            }
        }
    }

}
