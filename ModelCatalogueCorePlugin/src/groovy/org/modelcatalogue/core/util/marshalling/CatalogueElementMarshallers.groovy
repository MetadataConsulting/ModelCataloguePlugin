package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.*
import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by ladin on 14.02.14.
 */
abstract class CatalogueElementMarshallers extends AbstractMarshallers {

    @Autowired ReportsRegistry reportsRegistry
    @Autowired RelationshipTypeService relationshipTypeService
    @Autowired RelationshipService relationshipService

    CatalogueElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                latestVersionId: el.latestVersionId ?: el.id,
				modelCatalogueId: el.modelCatalogueId ?: el.defaultModelCatalogueId,
                archived: el.archived,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                classifiedName: relationshipService.getClassifiedName(el),
                ext: el.ext,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                relationships: [count: el.countRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"],
                outgoingRelationships: [count: el.countOutgoingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search"],
                incomingRelationships: [count: el.countIncomingRelations(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search"],
                versionNumber        : el.versionNumber,
                status               : el.status.toString(),
                versionCreated       : el.versionCreated,
                history              : [count: el.countVersions(), itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history"]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(el.getClass())

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        relationships.incoming?.each        addRelationsJson('incoming', el, ret, types)
        relationships.outgoing?.each        addRelationsJson('outgoing', el, ret, types)
        relationships.bidirectional?.each   addRelationsJson('relationships', el, ret, types)

        ret.availableReports = getAvailableReports(el)
        ret.classifications  = relationshipService.getClassificationsInfo(el)

        if (modelCatalogueSecurityService.currentUser) {
            RelationshipType favorite = RelationshipType.readByName('favourite')
            if (favorite) {
                Number count = Relationship.where {
                    relationshipType == favorite && destination == el && source == modelCatalogueSecurityService.currentUser
                }.count()
                if (count > 0) {
                    ret.favourite = true
                }
            }
        }

        ret

    }

    protected getRelationshipTypesFor(Class elementClass){
        relationshipTypeService.getRelationshipTypesFor(elementClass)
    }

    Map<String, Map<String, String>> getRelationshipConfiguration(Class type) {
        Map<String, Map<String, String>> relationships  = [incoming: [:], outgoing: [:], bidirectional: [:]]
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
                relationships.each { String direction, Map config ->
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


    static Map<String, Object> minimalCatalogueElementJSON(CatalogueElement element) {
        if (!element) return null
        [name: element.name, id: element.id, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id]
    }

    static Map<String, Object> minimumValueDomain(ValueDomain element) {
        if (!element) return null
        [name: element.name, id: element.id, dataType: minimumDataType(element.dataType), elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id]
    }

    static Map<String, Object> minimumDataType(DataType element) {
        if (!element) return null
        if (element instanceof EnumeratedType) [name: element.name, id: element.id, enumerations: element.enumerations,  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id]
        else [name: element.name, id: element.id,  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id]
    }


}
