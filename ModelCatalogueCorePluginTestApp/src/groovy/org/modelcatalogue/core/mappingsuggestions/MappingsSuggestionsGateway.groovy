package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState

@CompileStatic
interface MappingsSuggestionsGateway {
    MappingSuggestionResponse findAll(MappingSuggestionRequest mappingSuggestionRequest)
    Number count(MappingSuggestionCountRequest mappingSuggestionCountRequest, List<ActionState> state)
    void reject(List<Long> actionIds)
    void approve(List<Long> actionIds)
    void approveAll(Long batchId)
}
