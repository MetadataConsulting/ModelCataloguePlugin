package org.modelcatalogue.core.util.marshalling

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.*
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.RelationshipDirection
import org.springframework.beans.factory.annotation.Autowired
import org.modelcatalogue.core.util.OrderedMap

abstract class CatalogueElementMarshaller extends AbstractMarshaller {

    @Autowired RelationshipTypeService relationshipTypeService
    @Autowired RelationshipService relationshipService
    @Autowired DataModelService dataModelService

    CatalogueElementMarshaller(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                dataModel: minimalCatalogueElementJSON(el.dataModel),
                id: el.id,
                latestVersionId: el.latestVersionId ?: el.id,
				modelCatalogueId: el.modelCatalogueId ?: el.defaultModelCatalogueId,
				internalModelCatalogueId: el.defaultModelCatalogueId,
                archived: el.archived,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                classifiedName: getClassifiedName(el),
                ext: OrderedMap.toJsonMap(el.ext),
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                relationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships"],
                outgoingRelationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search"],
                incomingRelationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search"],
                versionNumber        : el.versionNumber,
                status               : el.status.toString(),
                versionCreated       : el.versionCreated,
                history              : [count: el.countVersions(), itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history"]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(el.getClass())

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        ret.outgoingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search"]
        ret.incomingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search"]

        relationships.incoming?.each        addRelationsJson(RelationshipDirection.INCOMING, el, ret, types)
        relationships.outgoing?.each        addRelationsJson(RelationshipDirection.OUTGOING, el, ret, types)
        relationships.bidirectional?.each   addRelationsJson(RelationshipDirection.BOTH, el, ret, types)

        ret.availableReports = getAvailableReports(el)

        // TODO: remove soon
        ret.dataModels = [ret.dataModel]

        if (dataModelService.legacyDataModels) {
            def oldDataModels = relationshipService.getDataModelsInfo(el)
            if (oldDataModels) {
                // for a review
                ret.originalDataModels = oldDataModels
            }
        }

        ret.favourite = relationshipService.isFavorite(el)

        return ret
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

    private static Closure addRelationsJson(RelationshipDirection direction, CatalogueElement el, Map ret, Map<String, RelationshipType> types) {
        { String relationshipType, String name ->
            RelationshipType type = types[relationshipType]
            def relation = [itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipType}", search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipType}/search"]
            switch (direction) {
                case RelationshipDirection.INCOMING:
                    relation.count = el.countIncomingRelationshipsByType(type)
                    break
                case RelationshipDirection.OUTGOING:
                    relation.count = el.countOutgoingRelationshipsByType(type)
                    break
                case RelationshipDirection.BOTH:
                    relation.count = el.countRelationshipsByType(type)
                    break
            }

            ret[name] = relation
        }
    }


    static Map<String, Object> minimalCatalogueElementJSON(CatalogueElement element) {
        if (!element) return null
        [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel)]
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataModel element) {
        if (!element) return null
        [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, semanticVersion: element.semanticVersion]
    }

    static Map<String, Object> minimumDataType(DataType element) {
        if (!element) return null
        if (element instanceof EnumeratedType) {
            return [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, enumerations: OrderedMap.toJsonMap(element.enumerations),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        if (element instanceof ReferenceType) {
            return [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, dataClass: minimalCatalogueElementJSON(element.dataClass),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        if (element instanceof PrimitiveType) {
            return [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, measurementUnit: minimalCatalogueElementJSON(element.measurementUnit),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        return [internalModelCatalogueId: element.defaultModelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id", status: "${element.status}", latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
    }


    static String getClassifiedName(CatalogueElement element) {
        if (!element) {
            return null
        }

        if (!element.id) {
            return element.name
        }

        if (element.dataModel) {
            return "${element.name} (${element.dataModel.name})"
        }

        return element.name
    }

}
