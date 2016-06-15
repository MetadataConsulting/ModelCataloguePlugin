package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.springframework.beans.factory.annotation.Autowired

abstract class CatalogueElementMarshaller extends AbstractMarshaller {

    @Autowired RelationshipTypeService relationshipTypeService
    @Autowired RelationshipService relationshipService
    @Autowired DataModelService dataModelService
    @Autowired ElementService elementService

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
                link:  el.link,
                relationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/relationships".toString()],
                outgoingRelationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search".toString()],
                incomingRelationships: [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search".toString()],
                versionNumber        : el.versionNumber,
                status               : el.status.toString(),
                versionCreated       : el.versionCreated,
                history              : [count: el.countVersions(), itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history".toString()],
                typeHierarchy        : [count: Integer.MAX_VALUE, itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/typeHierarchy".toString()]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(el.getClass())

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        ret.outgoingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search".toString()]
        ret.incomingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search".toString()]

        relationships.incoming?.each        addRelationsJson(RelationshipDirection.INCOMING, el, ret, types)
        relationships.outgoing?.each        addRelationsJson(RelationshipDirection.OUTGOING, el, ret, types)
        relationships.bidirectional?.each   addRelationsJson(RelationshipDirection.OUTGOING, el, ret, types)

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

        if (el.status == ElementStatus.PENDING) {
            BuildProgressMonitor monitor = BuildProgressMonitor.get(el.getId())

            if (monitor) {
                ret.htmlPreview = """
                    <pre>${monitor.lastMessages}</pre>
                """
            }

        }

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
            def relation = [itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipType}".toString()]
            if (type?.searchable) {
                relation.search = "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipType}/search".toString()
            }
            switch (direction) {
                case RelationshipDirection.INCOMING:
                    relation.count = el.countIncomingRelationshipsByType(type)
                    break
                default:
                    relation.count = el.countOutgoingRelationshipsByType(type)
                    break
            }

            ret[name] = relation
        }
    }


    static Map<String, Object> minimalCatalogueElementJSON(CatalogueElement element) {
        Class entityClass = HibernateHelper.getEntityClass(element)

        if (entityClass == DataModel) {
            return minimalCatalogueElementJSON(element as DataModel)
        } else if (entityClass == MeasurementUnit) {
            return minimalCatalogueElementJSON(element as MeasurementUnit)
        } else if (entityClass == DataElement) {
            return minimalCatalogueElementJSON(element as DataElement)
        } else if (DataType.isAssignableFrom(entityClass)) {
            return minimalCatalogueElementJSON(element as DataType)
        }
        if (!element) return null
        [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId,minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel)]
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataModel element) {
        if (!element) return null
        [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId,minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, semanticVersion: element.semanticVersion]
    }

    static Map<String, Object> minimalCatalogueElementJSON(MeasurementUnit element) {
        if (!element) return null
        [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId,minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, symbol: element.symbol]
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataElement element) {
        if (!element) return null
        [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId,minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), versionNumber: element.versionNumber, latestVersionId: element.latestVersionId ?: element.id, dataType: minimalCatalogueElementJSON(element.dataType)]
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataType element) {
        if (!element) return null
        Class cls = HibernateHelper.getEntityClass(element)
        if (cls == EnumeratedType) {
            return [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, enumerations: element.enumerationsObject.toJsonMap(),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        if (cls == ReferenceType) {
            return [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, dataClass: minimalCatalogueElementJSON(element.dataClass),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        if (cls == PrimitiveType) {
            return [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, measurementUnit: minimalCatalogueElementJSON(element.measurementUnit),  elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
        }
        return [dateCreated: element.dateCreated, versionCreated: element.versionCreated, lastUpdated: element.lastUpdated, ext: OrderedMap.toJsonMap(element.ext), internalModelCatalogueId: element.defaultModelCatalogueId, modelCatalogueId: element.modelCatalogueId, minimal: true, name: element.name, classifiedName: getClassifiedName(element), id: element.id, description: element.description, elementType: element.getClass().name, link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(), status: "${element.status}".toString(), latestVersionId: element.latestVersionId ?: element.id, dataModel: minimalCatalogueElementJSON(element.dataModel), versionNumber: element.versionNumber]
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
