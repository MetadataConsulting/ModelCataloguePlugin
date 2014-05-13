package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.hibernate.Criteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtendibleElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.ListAndCount

@Transactional
class DataArchitectService {

    def modelCatalogueSearchService, publishedElementService

    def uninstantiatedDataElements(Map params){
        ListAndCount results = new ListAndCount()
        def uninstantiatedDataElements, totalCount
        def instantiation = RelationshipType.findByName("instantiation")
        def searchParams = getParams(params)

            totalCount = DataElement.executeQuery("SELECT DISTINCT COUNT(a) FROM DataElement a " +
                    "WHERE a.outgoingRelationships IS EMPTY " +
                    "OR a NOT IN " +
                    "(SELECT a2 from DataElement a2 " +
                    "JOIN a2.outgoingRelationships e2 " +
                    "WHERE e2.relationshipType = ?)", [instantiation], [cache:true]
            )

            uninstantiatedDataElements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                    "WHERE a.outgoingRelationships IS EMPTY " +
                    "OR a NOT IN " +
                    "(SELECT a2 from DataElement a2 " +
                    "JOIN a2.outgoingRelationships e2 " +
                    "WHERE e2.relationshipType = ?)", [instantiation], [max: searchParams.max, offset: searchParams.offset]
            )

        results.count = (totalCount.get(0))?totalCount.get(0):0
        results.list = uninstantiatedDataElements


        return results
    }

    def metadataKeyCheck(Map params){

        def missingMetadataKey, totalCount
        ListAndCount results = new ListAndCount()
        def searchParams = getParams(params)

            totalCount = DataElement.executeQuery("SELECT DISTINCT COUNT(a) FROM DataElement a " +
                    "WHERE a.extensions IS EMPTY " +
                    "OR a NOT IN " +
                    "(SELECT a2 from DataElement a2 " +
                    "JOIN a2.extensions e2 " +
                    "WHERE e2.name = ?)", [searchParams.key], [cache:true]
            )

            missingMetadataKey = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                    "WHERE a.extensions IS EMPTY " +
                    "OR a NOT IN " +
                    "(SELECT a2 from DataElement a2 " +
                    "JOIN a2.extensions e2 " +
                    "WHERE e2.name = ?)", [searchParams.key], [max: searchParams.max, offset: searchParams.offset]
            )

            results.count = (totalCount.get(0))?totalCount.get(0):0
            results.list = missingMetadataKey


        return results
    }

    def indexAll(){
        modelCatalogueSearchService.index(ConceptualDomain.list())
        modelCatalogueSearchService.index(DataType.list())
        modelCatalogueSearchService.index(EnumeratedType.list())
        modelCatalogueSearchService.index(ExtensionValue.list())
        modelCatalogueSearchService.index(MeasurementUnit.list())
        modelCatalogueSearchService.index(ValueDomain.list())
        modelCatalogueSearchService.index(DataElement.list())
        modelCatalogueSearchService.index(Model.list())
        modelCatalogueSearchService.index(CatalogueElement)
        modelCatalogueSearchService.index(ExtendibleElement)
        modelCatalogueSearchService.index(PublishedElement)
        modelCatalogueSearchService.index(RelationshipType)
        modelCatalogueSearchService.index(Relationship)
        //TODO: find a better way of unindexing archived elements
        def params = [:]
        params.status = PublishedElementStatus.ARCHIVED
        def archivedElements = publishedElementService.list(params)
        if(archivedElements){ modelCatalogueSearchService.unindex(archivedElements) }
    }


    private static Map getParams(Map params){
        def searchParams = [:]
        if(params.key){searchParams.put("key" , params.key)}
        if(params.max){searchParams.put("max" , params.max.toInteger())}else{searchParams.put("max" , 10)}
       // if(params.sort){searchParams.put("sort" , "$params.sort")}else{searchParams.put("sort" , "name")}
       // if(params.order){searchParams.put("order" , params.order.toLowerCase())}else{searchParams.put("order" , "asc")}
        if(params.offset){searchParams.put("offset" , params.offset.toInteger())}else{searchParams.put("offset" , 0)}
        return searchParams
    }


    def getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    def getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def getValueDomain(DataElement dataElement){
        if(dataElement.instantiatedBy) {
            return dataElement.instantiatedBy.first()
        }
        return null
    }

    def getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            if (dataType instanceof EnumeratedType) {
                return dataType.enumAsString
            }
            return dataType.name
        }
        return null
    }

    def getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }

}
