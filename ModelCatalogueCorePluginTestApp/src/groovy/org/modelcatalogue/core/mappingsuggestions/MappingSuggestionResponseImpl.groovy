package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic

@CompileStatic
class MappingSuggestionResponseImpl implements MappingSuggestionResponse {
    Long id
    Long sourceId
    String sourceName
    Long destinationId
    String destinationName
    List<MappingSuggestion> mappingSuggestionList
}
