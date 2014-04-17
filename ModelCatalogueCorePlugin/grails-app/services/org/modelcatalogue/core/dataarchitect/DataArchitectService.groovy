package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.hibernate.Criteria
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

@Transactional
class DataArchitectService {

    def uninstantiatedDataElements(Map params){
        def results = [:]
        def uninstantiatedDataElements, totalCount
        def instantiation = RelationshipType.findByName("instantiation")
        def searchParams = getParams(params)
        //TODO change this query to an hql query to enable pagination with distinctness
        try {

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
            results.put("totalCount", totalCount.get(0).toInteger())
            results.put("results", uninstantiatedDataElements)

        }catch(Exception e){
            results.put("errors", e)
        }

        return results
    }

    def metadataKeyCheck(Map params){

        def missingMetadataKey, totalCount
        def results = [:]
        def searchParams = getParams(params)
        try {

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

            results.put("totalCount", totalCount.get(0).toInteger())
            results.put("results", missingMetadataKey)
        }catch(Exception e){
            results.put("errors", e)
        }

        return results
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



}
