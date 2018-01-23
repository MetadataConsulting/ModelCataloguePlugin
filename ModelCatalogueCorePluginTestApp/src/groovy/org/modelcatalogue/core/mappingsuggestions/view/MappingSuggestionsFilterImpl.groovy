package org.modelcatalogue.core.mappingsuggestions.view

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
class MappingSuggestionsFilterImpl implements MappingSuggestionsFilter {
    String term
    Integer max
    List<ActionState> stateList
    Integer score
}
