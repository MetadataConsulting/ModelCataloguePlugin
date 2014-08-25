package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists

class DataArchitectService {

    def modelCatalogueSearchService, relationshipService

    ListWithTotal<DataElement> uninstantiatedDataElements(Map params){
        Lists.fromCriteria(params, DataElement) {
            isNull 'valueDomain'
        }
    }

    ListWithTotal<DataElement> metadataKeyCheck(Map params){

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

    ListWithTotal<DataElement> findRelationsByMetadataKeys(String keyOne, String keyTwo, Map params){

        ListAndCount results = new ListAndCount()
        def searchParams = getParams(params)
        def synonymDataElements = []
        //FIXME the relationship type should be configurable
        def relType = RelationshipType.findByName("relatedTo")

        def key1Elements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                "inner join a.extensions e " +
                "WHERE e.name = ?)", [keyOne])//, [max: searchParams.max, offset: searchParams.offset])

        key1Elements.eachWithIndex { DataElement dataElement, int dataElementIndex ->
            def extensionName = dataElement.extensions[dataElement.extensions.findIndexOf {
                it.name == keyOne
            }].extensionValue
            def key2Elements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                    "inner join a.extensions e " +
                    "WHERE e.name = ? and e.extensionValue = ?) ", [keyTwo, extensionName], [max: searchParams.max, offset: searchParams.offset])

            // Create a Map
            key2Elements.each {
                //FIXME the relationship type needs to be configurable
                def relationship = new Relationship(source: dataElement, destination: it, relationshipType: relType)
                synonymDataElements << relationship
            }
        }

        results.list = synonymDataElements
        results.count = synonymDataElements.size()
        return results
    }

    def actionRelationshipList(ArrayList<Relationship> list){
        list.each { relationship ->
            relationship.save()
        }
    }

    private static Map getParams(Map params){
        def searchParams = [:]
        if(params.key){searchParams.put("key" , params.key)}
        if(params.keyOne){searchParams.put("keyOne" , params.keyOne)}
        if(params.keyTwo){searchParams.put("keyTwo" , params.keyTwo)}
        if(params.max){searchParams.put("max" , params.max.toInteger())}else{searchParams.put("max" , 10)}
       // if(params.sort){searchParams.put("sort" , "$params.sort")}else{searchParams.put("sort" , "name")}
       // if(params.order){searchParams.put("order" , params.order.toLowerCase())}else{searchParams.put("order" , "asc")}
        if(params.offset){searchParams.put("offset" , params.offset.toInteger())}else{searchParams.put("offset" , 0)}
        return searchParams
    }



}
