package org.modelcatalogue.core.actions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.ActionGormService
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.util.IdName

@CompileStatic
class BatchService {
    BatchGormService batchGormService
    ActionGormService actionGormService

    List<BatchViewModel> findAllActive() {
        List<Batch> batchList = batchGormService.findAllActive()
        batchList.collect { Batch batch ->
            Map m = [:]
            for (ActionState state in ActionState.values()) {
                m[state] = actionGormService.countByBatchAndState(batch, state)
            }
            new BatchViewModel(id: batch.id, lastUpdated: batch.lastUpdated, name: batch.name, actionStateCount: m)
        }
    }

    List<OptimizationType> findAllActiveOptimizationType() {
        [
                OptimizationType.DATA_ELEMENT_EXACT_MATCH,
                OptimizationType.ENUM_DUPLICATES_AND_SYNOYMS,
                OptimizationType.DATA_ELEMENT_FUZZY_MATCH
        ]
    }

    BatchCreateViewModel instantiateBatchCreateViewModel(List<IdName> dataModelList,
                                                         Long dataModel1Id,
                                                         Long dataModel2Id,
                                                         Integer minScore,
                                                         OptimizationType optimizationType) {
        Long firstId = dataModel1Id
        Long secondId = dataModel2Id
        if ( dataModelList && (!firstId || !dataModelList.find { IdName dataModel -> dataModel.id == firstId }) ) {
            firstId = dataModelList.first().id
        }

        if ( dataModelList && (!secondId || !dataModelList.find { IdName dataModel -> dataModel.id == secondId } && dataModelList.size() > 1 ) ) {
            secondId = dataModelList[1].id
        }
        new BatchCreateViewModel(
                minScore: minScore,
                dataModel1: firstId,
                dataModel2: secondId,
                optimizationType: optimizationType
        )
    }
}
