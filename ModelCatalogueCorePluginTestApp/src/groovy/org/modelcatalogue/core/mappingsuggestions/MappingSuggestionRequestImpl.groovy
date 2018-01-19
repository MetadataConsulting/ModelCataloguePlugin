package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
class MappingSuggestionRequestImpl implements MappingSuggestionRequest, MappingSuggestionCountRequest {
    Long batchId
    List<ActionState> stateList
    Integer max
    Integer offset
    Integer scorePercentage
    String term
}
