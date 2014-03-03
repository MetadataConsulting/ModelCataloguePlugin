package uk.co.mc.core


class SearchService {

    def elasticSearchService

    def search(Class resource, String query) {
        def results =  resource.search(query).searchResults
        return results
    }

    def search(String query){
        def results = elasticSearchService.search(query).searchResults
        return results
    }

}
