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
                elementType: HibernateHelper.getEntityClass(el).name,
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
                history              : [count: Integer.MAX_VALUE, itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history".toString()],
                typeHierarchy        : [count: Integer.MAX_VALUE, itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/typeHierarchy".toString()]
        ]

        Map<String, Map<String, String>> relationships = getRelationshipConfiguration(el.getClass())

        Map<String, RelationshipType> types = getRelationshipTypesFor(el.getClass())

        ret.outgoingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/outgoing/search".toString()]
        ret.incomingRelationships = [count: Integer.MAX_VALUE, itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming".toString(), search: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/incoming/search".toString()]

        relationships.incoming?.each        addRelationsJson(RelationshipDirection.INCOMING, el, ret, types)
        relationships.outgoing?.each        addRelationsJson(RelationshipDirection.OUTGOING, el, ret, types)
        relationships.bidirectional?.each   addRelationsJson(RelationshipDirection.OUTGOING, el, ret, types)

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

    /**
     * e.g. relationships = {
     *     "incoming": {
     *         "hierarchy": "childOf"
     *     }
     *     "outgoing": {
     *         "hierarchy": "parentOf"
     *     }
     *     "bidirectional": {
     *         "relatedTo": "relatedTo"
     *     }
     *  }
     * @param type
     * @return
     */
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

    private static Closure addRelationsJson(RelationshipDirection direction, CatalogueElement el, Map ret, Map<String, RelationshipType> relationshipTypeMap) {
        { String relationshipTypeName, String relationshipTypeDirectionalName ->
            RelationshipType relationshipType = relationshipTypeMap[relationshipTypeName]
            def directedRelationJSON = [itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipTypeName}".toString()]
            if (relationshipType?.searchable) {
                directedRelationJSON.search = "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/${direction.actionName}/${relationshipTypeName}/search".toString()
            }
            switch (direction) {
                case RelationshipDirection.INCOMING:
                    directedRelationJSON.count = el.countIncomingRelationshipsByType(relationshipType)
                    break
                default:
                    directedRelationJSON.count = el.countOutgoingRelationshipsByType(relationshipType)

                    break
            }

            ret[relationshipTypeDirectionalName] = directedRelationJSON
        }
    }

    static Map<String, Object> minimalCatalogueElementJSONSkeleton(CatalogueElement element) {
        return [dateCreated: element.dateCreated,
                versionCreated: element.versionCreated,
                lastUpdated: element.lastUpdated, /*ext: OrderedMap.toJsonMap(element.ext),*/
                internalModelCatalogueId: element.defaultModelCatalogueId,
                modelCatalogueId: element.modelCatalogueId,
                minimal: true,
                name: element.name,
                classifiedName: getClassifiedName(element),
                id: element.id,
                description: element.description,
                elementType: element.getClass().name,
                link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(),
                status: "${element.status}".toString(),
                versionNumber: element.versionNumber,
                latestVersionId: element.latestVersionId ?: element.id,
                dataModel: minimalCatalogueElementJSON(element.dataModel)]
    }


    static Map<String, Object> minimalDataTypeJSONSkeleton(DataType element) {
        if ( !element ) {
            return [:]
        }
        [ internalModelCatalogueId: element.defaultModelCatalogueId,
                 modelCatalogueId: element.modelCatalogueId,
                 minimal: true,
                 name: element.name,
                 id: element.id,
                 description: element.description,
                 elementType: element.getClass().name,
                 link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(),
                 status: "${element.status}".toString(),
                 dataModel: minimalDataModelJSONSkeleton(element.dataModel),
        ]
    }

    static Map<String, Object> minimalDataElementElementJSONSkeleton(DataElement element) {
        if ( !element ) {
            return [:]
        }
        [
                internalModelCatalogueId: element.defaultModelCatalogueId,
                modelCatalogueId: element.modelCatalogueId,
                minimal: true,
                name: element.name,
                id: element.id,
                description: element.description,
                elementType: element.getClass().name,
                link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(),
                status: "${element.status}".toString(),
                dataModel: minimalDataModelJSONSkeleton(element.dataModel)
        ]
    }

    static Map<String, Object> minimalDataModelJSONSkeleton(CatalogueElement element) {
        if ( !element ) {
            return [:]
        }
        [
                internalModelCatalogueId: element.defaultModelCatalogueId,
                modelCatalogueId: element.modelCatalogueId,
                name: element.name,
                id: element.id,
                description: element.description,
                elementType: element.getClass().name,
                link:  "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(),
        ] as Map<String, Object>
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
        } else if (Asset.isAssignableFrom(entityClass)) {
            return minimalCatalogueElementJSON(element as Asset)
        } else if (DataClass.isAssignableFrom(entityClass)) {
            return minimalCatalogueElementJSON(element as DataClass)
        } else if (!element) {
            return null
        } else {
            return minimalCatalogueElementJSONSkeleton(element)
        }
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataModel element) {
        if (!element) return null
        def minimalJSON = minimalCatalogueElementJSONSkeleton(element)
        minimalJSON.remove('dataModel')
        minimalJSON.put('semanticVersion', element.semanticVersion)
        return minimalJSON
    }

    static Map<String, Object> minimalCatalogueElementJSON(MeasurementUnit element) {
        if (!element) return null
        def minimalJSON = minimalCatalogueElementJSONSkeleton(element)
        minimalJSON.remove('dataModel')
        minimalJSON.put('symbol', element.symbol)
        return minimalJSON
    }


    static Map<String, Object> minimalCatalogueElementJSON(Asset element) {
        if (!element) return null
        def minimalJSON = minimalCatalogueElementJSONSkeleton(element)
        minimalJSON.remove('dataModel')
        minimalJSON.put('originalFileName', element.originalFileName)
        minimalJSON.put('contentType', element.contentType)
        minimalJSON.put('publishedStatus', "${element.publishedStatus}".toString() )
        return minimalJSON
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataElement element) {
        if (!element) return null
        def minimalJSON = minimalDataElementElementJSONSkeleton(element)
        minimalJSON.put('dataType', minimalCatalogueElementJSON(element.dataType))
        return minimalJSON
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataClass element) {
        if (!element) return null
        def minimalJSON = minimalCatalogueElementJSONSkeleton(element)
        DataClassMarshaller.addDataClassFields(minimalJSON, element) // needed to display hierarchy properly in CatalogueElementTreeview, particularly the "content" field. It doesn't seem like the other CatalogueElements like DataElement and PrimitiveType need this "content" field to display their DataTypes and MeasurementUnits respectively...

        return minimalJSON
    }

    static Map<String, Object> minimalCatalogueElementJSON(DataType element) {
        if ( !element ) {
            return null
        }
        Map<String, Object> minimalJSON = minimalDataTypeJSONSkeleton(element)

        Class cls = HibernateHelper.getEntityClass(element)

        minimalJSON = minimalJSON + [link: "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(cls))}/$element.id".toString(), 'elementType': cls.name]

        if (cls == EnumeratedType) {

            return minimalJSON + [enumerations: ((EnumeratedType) element).enumerationsObject.toJsonMap()]

        } else if (cls == ReferenceType) {

            return minimalJSON + [dataClass: minimalCatalogueElementJSON(((ReferenceType) element).dataClass)]

        } else if (cls == PrimitiveType) {

            return minimalJSON + [measurementUnit: minimalCatalogueElementJSON(((PrimitiveType) element).measurementUnit)]

        }

        return minimalJSON
    }


    static String getClassifiedName(CatalogueElement element) {
        if (!element) {
            return null
        }

        if (!element.id) {
            return element.name
        }

        if (element.dataModel) {
            return "${element.name} (${element.dataModel.name} ${element.dataModelSemanticVersion})"
        }

        return element.name
    }

}
