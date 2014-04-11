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
        //TODO change this query to an hql query to enable pagination with distinctness
        try {
            uninstantiatedDataElements = c.listDistinct(/*offset: searchParams.offset, max: searchParams.max*/) {
                createAlias('outgoingRelationships', 'outgoingRelationships', Criteria.LEFT_JOIN)
                or {
                    isEmpty("outgoingRelationships")
                    ne('outgoingRelationships.relationshipType', instantiation)
                }
                order(searchParams.sort, searchParams.order)
            }
            //FIXME this is a hack to enable pagination
            results.put("totalCount", uninstantiatedDataElements.size())
            def start, end
            start = (searchParams.offset)?:0
            end = (searchParams.max)?searchParams.max + start-1:10
            end = (end > uninstantiatedDataElements.size()-1)? uninstantiatedDataElements.size()-1 :end
            uninstantiatedDataElements = uninstantiatedDataElements[start..end]
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
        def key = searchParams.key
        def c = DataElement.createCriteria()
        //TODO change this query to an hql query to enable pagination with distinctness
        try {
            missingMetadataKey = c.listDistinct(/*offset :searchParams.offset, max: searchParams.max*/){
                createAlias('extensions', 'extensions', Criteria.LEFT_JOIN)
                or{
                    isEmpty("extensions")
                    ne('extensions.name', key)
                }
                order (searchParams.sort, searchParams.order)
            }
            //FIXME this is a hack to enable pagination
            results.put("totalCount", missingMetadataKey.size())
            def start, end
            start = (searchParams.offset)?:0
            end = (searchParams.max)?searchParams.max + start-1:10
            end = (end > missingMetadataKey.size()-1)? missingMetadataKey.size()-1 :end
            missingMetadataKey = missingMetadataKey[start..end]
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
        if(params.sort){searchParams.put("sort" , "$params.sort")}else{searchParams.put("sort" , "name")}
        if(params.order){searchParams.put("order" , params.order.toLowerCase())}else{searchParams.put("order" , "asc")}
        if(params.offset){searchParams.put("offset" , params.offset.toInteger())}else{searchParams.put("offset" , 0)}
        return searchParams
    }



}
