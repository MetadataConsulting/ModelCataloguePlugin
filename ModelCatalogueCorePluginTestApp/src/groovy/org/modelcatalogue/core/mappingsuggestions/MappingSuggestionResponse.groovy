package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic

@CompileStatic
interface MappingSuggestionResponse {
    Long id
    Long getSourceId()
    String getSourceName()
    Long getDestinationId()
    String getDestinationName()
    List<MappingSuggestion> getMappingSuggestionList()
}