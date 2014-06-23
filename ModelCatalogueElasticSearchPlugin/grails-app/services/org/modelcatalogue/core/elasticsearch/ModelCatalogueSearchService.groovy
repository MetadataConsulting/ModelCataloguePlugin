package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.SearchCatalogue

class ModelCatalogueSearchService implements SearchCatalogue{

    def elasticSearchService, elasticSearchAdminService

    def search(Class resource, Map params) {
        def searchResults = [:]
        def searchParams = getSearchParams(params)
        if(searchParams.errors){
            searchResults.put("errors" , searchParams.errors)
            return searchResults
        }
        try{
            searchResults = resource.search(searchParams){
                bool {
                    must {
                        query_string(query: params.search)
                    }
                    must_not {
                        terms status: ['archived', 'removed'], system: ['true']
                    }
                }
            }
        }catch(IllegalArgumentException e){
            searchResults.put("errors" , "Illegal argument: ${e.getMessage()}")
        }catch(Exception e){
            searchResults.put("errors" , e.getMessage())
        }

        return searchResults
    }

    def search(Map params){
        def searchResults = [:]
        def searchParams = getSearchParams(params)
        if(searchParams.errors){
            searchResults.put("errors" , searchParams.errors)
            return searchResults
        }
        searchParams.put("indices", "org.modelcatalogue.core")
        searchParams.put("types", ["org.modelcatalogue.core.Asset","org.modelcatalogue.core.DataElement", "org.modelcatalogue.core.ValueDomain", "org.modelcatalogue.core.ConceptualDomain", "org.modelcatalogue.core.DataType", "org.modelcatalogue.core.EnumeratedType", "org.modelcatalogue.core.MeasurementUnit", "org.modelcatalogue.core.Model"])
        try{
            searchResults = elasticSearchService.search(searchParams){
                bool {
                    must {
                        query_string(query: params.search)
                    }
                    must_not {
                        terms status: ['archived', 'removed']
                    }
                }
            }
        }catch(IllegalArgumentException e){
            searchResults.put("errors" , "Illegal argument: ${e.getMessage()}")
        }catch(Exception e){
            searchResults.put("errors" , e.getMessage())
        }

        return searchResults
    }

    private static Map getSearchParams(Map params){
        def searchParams = [:]
        if(!params.search){
            searchParams.put("errors" , "No query string to search on")
            return searchParams
        }
        if(params.max){ searchParams.put("size" , "$params.max")}
        if(params.sort){searchParams.put("sort" , "name")}
        if(params.order){searchParams.put("order" , params.order.toLowerCase())}
        if(params.offset){searchParams.put("from" , "$params.offset")}
        return searchParams
    }

    //TODO add a few more of these

    def index(Class resource){
        elasticSearchService.index(resource)
    }

    def index(Collection<Class> resource){
        elasticSearchService.index(resource)
    }

    def unindex(Object object){
        elasticSearchService.unindex(object)
    }

    def unindex(Collection<Object> object){
        elasticSearchService.unindex(object)
    }

    def refresh(){
        elasticSearchAdminService.refresh()
    }


}
