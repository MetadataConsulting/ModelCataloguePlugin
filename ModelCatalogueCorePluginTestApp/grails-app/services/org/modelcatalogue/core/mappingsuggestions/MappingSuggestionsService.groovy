package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.MetadataDomainEntityService
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.ActionService
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.persistence.ActionGormService
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataModelPolicyGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.persistence.ExtensionValueGormService
import org.modelcatalogue.core.persistence.MappingGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.modelcatalogue.core.persistence.ReferenceTypeGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class MappingSuggestionsService implements MappingsSuggestionsGateway {

    MessageSource messageSource

    MetadataDomainEntityService metadataDomainEntityService

    BatchGormService batchGormService

    ActionGormService actionGormService

    ActionService actionService

    @Override
    MappingSuggestionResponse findAll(MappingSuggestionRequest req) {
        Batch batch = batchGormService.findById(req.batchId)

        List<ActionState> stateList = (req.stateList ?: []) as List<ActionState>

        List<Action> actionList = actionGormService.findAllByBatch(batch, stateList, req.offset, req.max)
        IdName sourceIdName
        IdName destinationIdName
        if ( actionList ) {
            Action firstAction = actionList.first()
            CatalogueElement source = sourceCatalogueElementOfAction(firstAction)
            sourceIdName = dataModelIdName(source)

            CatalogueElement destination = destinationCatalogueElementOfAction(firstAction)
            destinationIdName = dataModelIdName(destination)
        }

        List<MappingSuggestion> suggestionList = actionList.collect { Action actionInstance ->
            mappingSuggestionOfAction(actionInstance)
        } as List<MappingSuggestion>


        new MappingSuggestionResponseImpl(id: batch.id,
                sourceId: sourceIdName?.id,
                sourceName: sourceIdName?.name,
                destinationId: destinationIdName?.id,
                destinationName: destinationIdName?.name,
                mappingSuggestionList: suggestionList)
    }

    IdName dataModelIdName(CatalogueElement catalogueElement) {
        new IdName(id: catalogueElement.dataModel?.id,
                name: "${catalogueElement.dataModel?.name} ${catalogueElement.dataModel?.semanticVersion}".toString())
    }

    CatalogueElement sourceCatalogueElementOfAction(Action actionInstance) {
        ActionParameter source = actionInstance.extensions.find { ActionParameter actionParameter -> actionParameter.name == 'source' }
        catalogueElementOfActionParameter(source)
    }

    CatalogueElement destinationCatalogueElementOfAction(Action actionInstance) {
        ActionParameter destination = actionInstance.extensions.find { ActionParameter actionParameter -> actionParameter.name == 'destination' }
        catalogueElementOfActionParameter(destination)
    }

    @Override
    Number count(MappingSuggestionCountRequest req, List<ActionState> states) {
        Batch batch = batchGormService.findById(req.batchId)
        if(states && states?.size()>0) {
            return actionGormService.countByBatchAndStates(batch, states)
        }
        actionGormService.countByBatch(batch)
    }

    @Override
    void reject(List<Long> actionIds) {
        List<Action> actionList = actionGormService.findAllByIds(actionIds)
        dismissActionList(actionList)
    }

    @Override
    void approve(List<Long> actionIds) {
        List<Action> actionList = actionGormService.findAllByIds(actionIds)
        runActionList(actionList)
    }

    @Override
    void approveAll(Long batchId) {
        List<Action> actionList = actionGormService.findAllByBatchId(batchId)
        runActionList(actionList)
    }

    protected void dismissActionList(List<Action> actionList) {
        for (Action action : actionList) {
            actionService.dismiss(action)
        }
    }
    protected void runActionList(List<Action> actionList) {
        for ( Action action : actionList ) {
            actionService.run(action)
        }
    }

    MappingSuggestion mappingSuggestionOfAction(Action actionInstance) {
        CatalogueElement sourceCatalogueElement = sourceCatalogueElementOfAction(actionInstance)
        CatalogueElement destinationCatalogueElement = destinationCatalogueElementOfAction(actionInstance)
        ElementCompared sourceCompared = instantiateElementCompared(sourceCatalogueElement)
        ElementCompared destinationCompared = instantiateElementCompared(destinationCatalogueElement)

        ActionParameter score = actionInstance.extensions.find { ActionParameter actionParameter -> actionParameter.name == 'matchScore' }
        new MappingSuggestionImpl(
                mappingSuggestionId: actionInstance.id,
                source: sourceCompared,
                destination: destinationCompared,
                score: score.extensionValue as float,
                state: actionInstance.state
        )
    }

    CatalogueElement catalogueElementOfActionParameter(ActionParameter actionParameter) {
        MetadataDomainEntity domainEntity = MetadataDomainEntity.of(actionParameter.extensionValue)
        metadataDomainEntityService.findByMetadataDomainEntity(domainEntity)
    }

    ElementCompared instantiateElementCompared(CatalogueElement catalogueElement) {
        if ( catalogueElement == null ) {
            return null
        }
        MetadataDomainEntity metadataDomainEntity = new MetadataDomainEntity(id: catalogueElement.id,
                domain: MetadataDomain.of(catalogueElement))
        new ElementComparedImpl(metadataDomainEntity: metadataDomainEntity,
                code: catalogueElement.modelCatalogueId ?: "${catalogueElement.id}" as String,
                name: catalogueElement.name
        )
    }
}
