package org.modelcatalogue.core

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService{

    def search(Class resource, Map params) {
        if (!params.search) {
            return [errors: "No query string to search on"]
        }
        def searchResults = [:]

        String query                = "%$params.search%"
        if (CatalogueElement.isAssignableFrom(resource)) {
            searchResults.searchResults = resource.findAllByNameIlikeOrDescriptionIlike(query, query, params)
            searchResults.total = resource.countByNameIlikeOrDescriptionIlike(query, query, params)
        } else if (RelationshipType.isAssignableFrom(resource)) {
            searchResults.searchResults = resource.findAllByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query, params)
            searchResults.total = resource.countByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query, params)
        } else {
            searchResults.searchResults = resource.findAllByNameIlike(query, params)
            searchResults.total         = resource.countByNameIlike(query, params)
        }

        searchResults
    }

    def search(Map params){
        search CatalogueElement, params
    }

}
