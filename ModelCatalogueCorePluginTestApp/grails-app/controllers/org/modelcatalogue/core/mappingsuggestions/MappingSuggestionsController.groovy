package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.mappingsuggestions.view.MappingSuggestionsFilter
import org.modelcatalogue.core.mappingsuggestions.view.MappingSuggestionsFilterImpl
import org.modelcatalogue.core.view.PaginationViewModel
import org.modelcatalogue.core.view.PaginationViewModelImpl

import javax.annotation.PostConstruct

@CompileStatic
class MappingSuggestionsController {

    static allowedMethods = [
            index: 'GET',
            reject: 'POST',
            approve: 'POST',
            approveAll: 'POST',
    ]

    int defaultMax
    int defaultScore

    MappingsSuggestionsGateway mappingsSuggestionsGateway

    @CompileDynamic
    @PostConstruct
    void setup() {
        defaultMax = grailsApplication.config.mdx.mappingsuggestions.max ?: 20
        defaultScore = grailsApplication.config.mdx.mappingsuggestions.score ?: 20
    }

    protected List<ActionState> defaultActionStates() {
        ActionState.values()  as List<ActionState>
    }

    def index(MappingSuggestionIndexCommand cmd) {
        if ( cmd.hasErrors() ) {
            render status: 422
            return
        }
        MappingSuggestionRequest mappingSuggestionRequest = new MappingSuggestionRequestImpl(
                batchId: cmd.batchId,
                max: cmd.max ?: defaultMax,
                offset: cmd.offset ?: 0,
                scorePercentage: cmd.score ?: defaultScore,
                stateList: cmd.status ?: defaultActionStates(),
                term: cmd.term
        )
        MappingSuggestionResponse rsp = mappingsSuggestionsGateway.findAll(mappingSuggestionRequest)
        Number total = mappingsSuggestionsGateway.count(mappingSuggestionRequest)
        MappingSuggestionsFilter mappingSuggestionsFilter = new MappingSuggestionsFilterImpl(
                score: mappingSuggestionRequest.scorePercentage,
                stateList: mappingSuggestionRequest.stateList,
                term: mappingSuggestionRequest.term,
                max: mappingSuggestionRequest.max)
        PaginationViewModel pagination = new PaginationViewModelImpl(total: total ?: 0,
                max: mappingSuggestionRequest.max,
                offset: mappingSuggestionRequest.offset)
        [
                pagination: pagination,
                filter: mappingSuggestionsFilter,
                mappingSuggestionList: rsp?.mappingSuggestionList ?: [],
                sourceName: rsp.sourceName,
                destinationName: rsp.destinationName,
                batchId: cmd.batchId,
                sourceId: rsp.sourceId,
                destinationId: rsp.destinationId,
        ]
    }

    def reject(MappingSuggestionRejectCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect action: 'index', params: [batchId: cmd.batchId]
            return
        }

        mappingsSuggestionsGateway.reject(cmd.mappingSuggestionIds)
        redirect action: 'index', params: [batchId: cmd.batchId]
    }

    def approve(MappingSuggestionApproveCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect action: 'index', params: [batchId: cmd.batchId]
            return
        }

        mappingsSuggestionsGateway.approve(cmd.mappingSuggestionIds)
        redirect action: 'index', params: [batchId: cmd.batchId]
    }
}