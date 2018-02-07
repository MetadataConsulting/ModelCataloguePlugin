package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic

@CompileStatic
class MappingGenerationConfiguration {
    MatchAgainst matchAgainst
    int pageSizeSource
    int pageSizeDestination
    int maxSuggestions
    float minDistance
}
