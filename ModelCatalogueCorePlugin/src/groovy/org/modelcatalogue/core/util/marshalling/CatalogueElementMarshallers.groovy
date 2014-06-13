package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.reports.ReportDescriptor
import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by ladin on 14.02.14.
 */
abstract class CatalogueElementMarshallers extends AbstractMarshallers {

    @Autowired ReportsRegistry reportsRegistry

    CatalogueElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                archived: el.archived,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                relationships: [count: el.countRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"],
                outgoingRelationships: [count: el.countOutgoingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"],
                incomingRelationships: [count: el.countIncomingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(type)

        relationships.incoming?.each addRelationsJson('incoming', el, ret)
        relationships.outgoing?.each addRelationsJson('outgoing', el, ret)

        ret.availableReports = getAvailableReports(el)

        ret

    }

    protected getAvailableReports(CatalogueElement el) {
        def reports = []

        for (ReportDescriptor descriptor in reportsRegistry.getAvailableReports(el)) {
            reports << [title: descriptor.title, url: descriptor.getLink(el)]
        }

        reports
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
            relationships count: (el.countRelations()), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"
            outgoingRelations count: el.countOutgoingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing"
            incomingRelations count: el.countIncomingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming"
        }

        def relationships = getRelationshipConfiguration(type)

        relationships.incoming?.each addRelationsXml('incoming', el, xml)
        relationships.outgoing?.each addRelationsXml('outgoing', el, xml)
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.archived, "archived", xml)
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
