package org.modelcatalogue.core.util

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SearchParams {
    String search
    String status
    Long dataModelId
    ParamArgs paramArgs
    String elementType
    String type
    String searchImports
    String contentType
    Double minScore
    Boolean explain

    public static SearchParams of(GrailsParameterMap params, ParamArgs paramArgsParameter) {
        SearchParams searchParams = new SearchParams()
        searchParams.search = params.search
        searchParams.status = params.status
        searchParams.dataModelId = params.long('dataModelId')
        if ( !searchParams.dataModelId ) {
            searchParams.dataModelId = params.long('dataModel')
        }
        searchParams.paramArgs = paramArgsParameter
        searchParams.elementType = params.elementType
        searchParams.type = params.type
        searchParams.searchImports = params.searchImports
        searchParams.contentType = params.contentType
        searchParams.minScore = params.double('minScore')
        searchParams.explain = params.boolean('explain')
        return searchParams
    }
}
