package org.modelcatalogue.core

import grails.transaction.Transactional
import org.hibernate.Criteria

@Transactional
class DataArchitectService {

    def uninstantiatedDataElements(Map params){
        def results = [:]
        def uninstantiatedDataElements
        def instantiation = RelationshipType.findByName("instantiation")
        def searchParams = getParams(params)
        def c = DataElement.createCriteria()
        try {
            uninstantiatedDataElements = c.list(offset: searchParams.offset, max: searchParams.max) {
                createAlias('outgoingRelationships', 'outgoingRelationships', Criteria.LEFT_JOIN)
                or {
                    isEmpty("outgoingRelationships")
                    ne('outgoingRelationships.relationshipType', instantiation)
                }
                order(searchParams.sort, searchParams.order)
            }
            results.put("results", uninstantiatedDataElements)
        }catch(Exception e){
            results.put("errors", e)
        }

        return results
    }

    def metadataKeyCheck(Map params){

        def missingMetadataKey
        def results = [:]
        def searchParams = getParams(params)
        def c = DataElement.createCriteria()
        try {
            missingMetadataKey = c.list(offset :searchParams.offset, max: searchParams.max){
                createAlias('extensions', 'extensions', Criteria.LEFT_JOIN)
                or{
                    isEmpty("extensions")
                    ne('extensions.name', params.key)
                }
                order (searchParams.sort, searchParams.order)
            }
            results.put("results", missingMetadataKey)
        }catch(Exception e){
            results.put("errors", e)
        }

        return results
    }


    private static Map getParams(Map params){
        def searchParams = [:]
        if(params.max){searchParams.put("max" , params.max.toInteger())}else{searchParams.put("max" , 10)}
        if(params.sort){searchParams.put("sort" , "$params.sort")}else{searchParams.put("sort" , "name")}
        if(params.order){searchParams.put("order" , params.order.toLowerCase())}else{searchParams.put("order" , "asc")}
        if(params.offset){searchParams.put("offset" , params.offset.toInteger())}else{searchParams.put("offset" , 0)}
        return searchParams
    }



}
