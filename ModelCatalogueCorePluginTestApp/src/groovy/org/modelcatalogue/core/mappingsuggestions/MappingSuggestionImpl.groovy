package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
class MappingSuggestionImpl implements MappingSuggestion {
    Long mappingSuggestionId
    ElementCompared source
    ElementCompared destination
    ActionState state
    float score
}
