package uk.co.mc.core

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject


class SearchService {

    def elasticSearchService

    def search(Class resource, Map params) {
        def searchResults =  resource.search(){
            bool {
                must {
                    query_string(query: params.search)
                }
            }
            if(params.max){
            size : "$params.max"
            }
            if(params.sort){
                sort : ["order": ((params.order)?params.order.toLowerCase():"asc")]
            }
            if(params.offset){
                from : "$params.offset"
            }
        }.searchResults

        return searchResults
    }

    def search(Map params){
        def searchResults = elasticSearchService.search(){
            bool {
                must {
                    query_string(query: params.search)
                }
            }
            if(params.max){
                size : "$params.max"
            }
            if(params.sort){
                sort : ["order": ((params.order)?params.order.toLowerCase():"asc")]
            }
            if(params.offset){
                from : "$params.offset"
            }
        }.searchResults

        return searchResults
    }

}
