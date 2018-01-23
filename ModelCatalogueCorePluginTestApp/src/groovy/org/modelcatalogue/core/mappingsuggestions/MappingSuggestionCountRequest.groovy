package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
interface MappingSuggestionCountRequest {
    Long getBatchId()
    List<ActionState> getStateList()
    Integer getScorePercentage()
    String getTerm()
}