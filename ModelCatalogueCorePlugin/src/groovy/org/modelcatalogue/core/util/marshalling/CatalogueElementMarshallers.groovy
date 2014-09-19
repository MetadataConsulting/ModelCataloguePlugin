package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeService
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.CatalogueElementFinder
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by ladin on 14.02.14.
 */
abstract class CatalogueElementMarshallers extends AbstractMarshallers {

    @Autowired ReportsRegistry reportsRegistry
    @Autowired RelationshipTypeService relationshipTypeService

    CatalogueElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
				modelCatalogueId: el.modelCatalogueId,
                archived: el.archived,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                relationships: [count: el.countRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"],
                outgoingRelationships: [count: el.countOutgoingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search"],
                incomingRelationships: [count: el.countIncomingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search"]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(el.getClass())

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        relationships.incoming?.each        addRelationsJson('incoming', el, ret, types)
        relationships.outgoing?.each        addRelationsJson('outgoing', el, ret, types)
        relationships.bidirectional?.each   addRelationsJson('relationships', el, ret, types)

        ret.availableReports = getAvailableReports(el)

        ret

    }

    protected getRelationshipTypesFor(Class elementClass){
        relationshipTypeService.getRelationshipTypesFor(elementClass)
    }

    Map<String, Map<String, String>> getRelationshipConfiguration(Class type) {
        def relationships  = [incoming: [:], outgoing: [:], bidirectional: [:]]
        if (type.superclass && CatalogueElement.isAssignableFrom(type.superclass)) {
            def fromSuperclass = getRelationshipConfiguration(type.superclass)
            relationships.incoming.putAll(fromSuperclass.incoming ?: [:])
            relationships.outgoing.putAll(fromSuperclass.outgoing ?: [:])
            relationships.bidirectional.putAll(fromSuperclass.bidirectional ?: [:])
        }
        def fromType = GrailsClassUtils.getStaticFieldValue(type, 'relationships') ?: [incoming: [:], outgoing: [:], bidirectional: [:]]
        relationships.incoming.putAll(fromType.incoming ?: [:])
        relationships.outgoing.putAll(fromType.outgoing ?: [:])
        relationships.bidirectional.putAll(fromType.bidirectional ?: [:])

        getRelationshipTypesFor(type).each { String name, RelationshipType relationshipType ->
            if (relationshipType.system) {
                relationships.each { String direction, Map<String, String> config ->
                    config.remove name
                }
                return
            }

            if (relationshipType.bidirectional) {
                if (!relationships.bidirectional.containsKey(name)) {
                    relationships.incoming.remove(name)
                    relationships.outgoing.remove(name)

                    relationships.bidirectional[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                }
            } else {
                if (relationshipType.sourceClass.isAssignableFrom(type)) {
                    if (!relationships.outgoing.containsKey(name)){
                        relationships.bidirectional.remove(name)

                        relationships.outgoing[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                    }
                }
                if (relationshipType.destinationClass.isAssignableFrom(type)) {
                    if (!relationships.incoming.containsKey(name)) {

                        relationships.bidirectional.remove(name)

                        relationships.incoming[name] = RelationshipType.toCamelCase(relationshipType.destinationToSource)
                    }
                }
            }
        }

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

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        relationships.incoming?.each        addRelationsXml('incoming', el, xml, types)
        relationships.outgoing?.each        addRelationsXml('outgoing', el, xml, types)
        relationships.bidirectional?.each   addRelationsXml('relationships', el, xml, types)
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
		addXmlAttribute(el.modelCatalogueId, "modelCatalogueId", xml)
        addXmlAttribute(el.archived, "archived", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute(el.dateCreated, "dateCreated", xml)
        addXmlAttribute(el.lastUpdated, "lastUpdated", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
    }

    private static Closure addRelationsJson(String incomingOrOutgoing, CatalogueElement el, Map ret, Map<String, RelationshipType> types) {
        { String relationshipType, String name ->
            RelationshipType type = types[relationshipType]
            def relation = [itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}/search"]
            switch (incomingOrOutgoing) {
                case 'relationships':
                    relation.count = el.countRelationsByType(type)
                    break
                case 'incoming':
                    relation.count = el.countIncomingRelationsByType(type)
                    break
                case 'outgoing':
                    relation.count = el.countOutgoingRelationsByType(type)
                    break
            }

            ret[name] = relation
        }
    }

    private static Closure addRelationsXml(String incomingOrOutgoing, CatalogueElement el, XML xml, Map<String, RelationshipType> types) {
        { String relationshipType, String name ->
            RelationshipType type = types[relationshipType]
            def relation = [itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${incomingOrOutgoing}/${relationshipType}"]
            switch (incomingOrOutgoing) {
                case 'relationships':
                    relation.count = el.countRelationsByType(type)
                    break
                case 'incoming':
                    relation.count = el.countIncomingRelationsByType(type)
                    break
                case 'outgoing':
                    relation.count = el.countOutgoingRelationsByType(type)
                    break
            }
            xml. build {
                "${name}" relation
            }
        }
    }


    static Map<String, Object> minimalCatalogueElementJSON(CatalogueElement element) {
        if (!element) return null
        [name: element.name, id: element.id, elementType: element.getClass().name, link:  "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id"]
    }

}
