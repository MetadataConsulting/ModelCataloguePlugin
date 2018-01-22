package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic

@CompileStatic
interface MappingSuggestionRequest extends MappingSuggestionCountRequest {
    Integer getMax()
    Integer getOffset()
}