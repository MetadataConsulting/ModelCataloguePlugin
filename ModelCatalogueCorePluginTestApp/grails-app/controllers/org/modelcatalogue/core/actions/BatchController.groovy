package org.modelcatalogue.core.actions

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.dataarchitect.DataArchitectService
import org.modelcatalogue.core.mappingsuggestions.MapppingSuggestionsConfigurationService
import org.modelcatalogue.core.mappingsuggestions.MappingSuggestionRequest
import org.modelcatalogue.core.mappingsuggestions.MappingSuggestionRequestImpl
import org.modelcatalogue.core.mappingsuggestions.MappingSuggestionResponse
import org.modelcatalogue.core.mappingsuggestions.MappingsSuggestionsGateway
<<<<<<< HEAD
<<<<<<< HEAD
import org.modelcatalogue.core.mappingsuggestions.MatchAgainst
=======
>>>>>>> workaround issues with batch names
=======
import org.modelcatalogue.core.mappingsuggestions.MapppingSuggestionsConfigurationService
>>>>>>> Display mapping suggestion being used at the bottom fo the page
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.context.MessageSource
import org.springframework.validation.ObjectError
<<<<<<< HEAD
import javax.annotation.PostConstruct

=======
>>>>>>> Display mapping suggestion being used at the bottom fo the page
import javax.annotation.PostConstruct

@Slf4j
class BatchController extends AbstractRestfulController<Batch> {

    def actionService
    BatchService batchService
    BatchGormService batchGormService
    DataModelGormService dataModelGormService
    DataArchitectService dataArchitectService
    MessageSource messageSource
    def executorService
    MapppingSuggestionsConfigurationService mapppingSuggestionsConfigurationService
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
    MapppingSuggestionsConfigurationService mapppingSuggestionsConfigurationService

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


    static allowedMethods = [
            all: 'GET',
            create: 'GET',
            generateSuggestions: 'POST',
            archive: 'POST',
            index: 'GET',
            actions: 'GET',
            run: 'POST',
            reactivate: 'POST',
            dismiss: 'POST',
            updateActionParameters: 'PUT',
            addDependency: 'POST',
            removeDependency: 'DELETE'
    ]

    BatchController() {
        super(Batch)
    }

    def all() {
        List<BatchViewModel> batchList = batchService.findAllActive()

<<<<<<< HEAD

=======
>>>>>>> workaround issues with batch names
        for ( BatchViewModel batch : batchList ) {
            MappingSuggestionRequest mappingSuggestionRequest = new MappingSuggestionRequestImpl(
                    batchId: batch.id,
                    max: 1,
                    offset: 0,
                    scorePercentage: defaultScore,
                    stateList: defaultActionStates(),
                    term:  null
            )
            MappingSuggestionResponse rsp = mappingsSuggestionsGateway.findAll(mappingSuggestionRequest)
            if ( rsp && rsp.sourceName && rsp.destinationName) {
                String suffix = ''
                if ( batch.name ) {
                    if ( batch.name.contains('Fuzzy') ) {
                        suffix = ' - Fuzzy'
                    }
                    if ( batch.name.contains('Exact Matches') ) {
                        suffix = ' - Exact Matches'
                    }
                }
                batch.name = "${rsp.sourceName} vs ${rsp.destinationName} ${suffix}"
            }
        }

<<<<<<< HEAD

=======
>>>>>>> workaround issues with batch names
        Number total = batchGormService.countActive()
        [
                batchList: batchList,
                total: total,
                matchAgainst: mapppingSuggestionsConfigurationService.matchAgainst
        ]
    }

    def create() {
        List<IdName> dataModelList = dataModelGormService.findAll().collect { DataModel dataModel ->
            new IdName(id: dataModel.id, name: "${dataModel.name} ${dataModel.semanticVersion}".toString())
        }
        if ( dataModelList && dataModelList.size() < 2 ) {
            flash.error = messageSource.getMessage('batch.create.dataModel.min', [] as Object[], 'You need at least two Data Models', request.locale)
            redirect(action: 'all', controller: 'batch')
            return
        }
        Long dataModel1Id = params.long('dataModel1')
        Long dataModel2Id = params.long('dataModel2')
        Integer minScore = params.int('minScore') ?: 10
        OptimizationType optimizationType = params.optimizationType as OptimizationType

        BatchCreateViewModel batchCreateViewModel = batchService.instantiateBatchCreateViewModel(dataModelList, dataModel1Id, dataModel2Id, minScore, optimizationType)
        [
                batchCreateViewModel: batchCreateViewModel,
                dataModelList: dataModelList,
                optimizationTypeList: batchService.findAllActiveOptimizationType(),
        ]
    }

    def generateSuggestions(GenerateSuggestionsCommand cmd) {
        if (cmd.hasErrors()) {
            flash.error = cmd.errors.allErrors.collect { ObjectError error ->
                messageSource.getMessage(error, request.locale)
            }.join(',')
            redirect(action: 'create', controller: 'batch')
            return
        }

        executorService.execute {
            dataArchitectService.generateSuggestions(cmd.optimizationType, cmd.dataModel1ID, cmd.dataModel2ID, cmd.minScore)
        }

        flash.message = messageSource.getMessage('batch.generating', [] as Object[], 'Mappings being generated', request.locale)
        redirect action: 'all', controller: 'batch'
    }


    def archive(ArchiveCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = messageSource.getMessage('batch.archived.min', [] as Object[], 'Select at least one', request.locale)
            redirect action: 'all', controller: 'batch'
            return
        }

        batchGormService.update(cmd.batchIds, Boolean.TRUE)

        flash.message = messageSource.getMessage('batch.archived', [] as Object[], 'Mappings archived', request.locale)

        redirect action: 'all', controller: 'batch'
    }

    def runAll() {

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = findById(params.long('id'))

        if (!batch) {
            notFound()
            return
        }

        for (Action action in batch.actions) {
            actionService.run(action)
        }

        respond batch
    }

    @Override
    def index(Integer max) {

        handleParams(max)
        respond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'archived', params.boolean('archived') || params.status == 'archived'
        }
    }

    def updateActionParameters() {


        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        if (action.state in [ActionState.PERFORMING, ActionState.PERFORMED]) {
            methodNotAllowed()
            return
        }
        actionService.updateParameters(action, request.getJSON() as Map<String, String>)

        if (action.hasErrors()) {
            respond action.errors
            return
        }
        respond action
    }

    def listActions(Integer max) {

        handleParams(max)

        if (!params.id) {
            notFound()
            return
        }

        Batch batch = Batch.get(params.long('id'))

        if (!batch) {
            notFound()
            return
        }

        ActionState state = params.state ? ActionState.valueOf(params.state.toString().toUpperCase()) : null

        respond Lists.wrap(params, "/${resourceName}/${batch.id}/actions/${params?.state ?: ''}", actionService.list(params, batch, state))
    }

    def dismiss() {


        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.dismiss(action)
        ok()
    }

    def reactivate() {

        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.reactivate(action)
        ok()
    }

    def run() {

        if (!params.actionId) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }

        actionService.run(action)
        ok()
    }

    def addDependency() {

        if (!params.actionId || !params.providerId || !params.role) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }



        Action provider = Action.get(params.long('providerId'))

        if (!provider) {
            notFound()
            return
        }


        ActionDependency dependency = actionService.addDependency(action, provider, params.role.toString())

        if (!dependency) {
            methodNotAllowed()
        }

        respond action
    }

    def removeDependency() {

        if (!params.actionId || !params.role) {
            notFound()
            return
        }

        Action action = Action.get(params.long('actionId'))

        if (!action) {
            notFound()
            return
        }


        ActionDependency dependency = actionService.removeDependency(action, params.role.toString())

        if (!dependency) {
            notFound()
            return
        }

        respond action
    }

    protected Batch findById(long id) {
        batchGormService.findById(id)
    }
}
