package org.modelcatalogue.core.mappingsuggestions.view

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
interface MappingSuggestionsFilter {
    String getTerm()
    Integer getMax()
    List<ActionState> getStateList()
    Integer getScore()

}
