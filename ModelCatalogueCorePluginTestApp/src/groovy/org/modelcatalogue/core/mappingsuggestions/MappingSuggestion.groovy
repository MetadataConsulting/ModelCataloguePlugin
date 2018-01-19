package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
interface MappingSuggestion {
    Long getMappingSuggestionId()
    ElementCompared getSource()
    ElementCompared getDestination()
    ActionState getState()
    float getScore()
}