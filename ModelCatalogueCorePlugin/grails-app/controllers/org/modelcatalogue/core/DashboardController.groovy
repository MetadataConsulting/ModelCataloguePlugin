package org.modelcatalogue.core

import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.Lists

class DashboardController {

    static responseFormats = ['json', 'xlsx']
    def dataArchitectService
    def classificationService

    def index() {

        def uninstantiatedDataElements = dataArchitectService.uninstantiatedDataElements(params)

        def model = [
                totalDataElementCount: countWithClassification(DataElement),
                draftDataElementCount: countWithClassificationAndStatus(DataElement, ElementStatus.DRAFT),
                finalizedDataElementCount: countWithClassificationAndStatus(DataElement, ElementStatus.FINALIZED),
                totalAssetCount: countWithClassification(Asset),
                draftAssetCount: countWithClassificationAndStatus(Asset, ElementStatus.DRAFT),
                finalizedAssetCount: countWithClassificationAndStatus(Asset, ElementStatus.FINALIZED),
                totalModelCount:countWithClassification(Model),
                draftModelCount: countWithClassificationAndStatus(Model, ElementStatus.DRAFT),
                finalizedModelCount: countWithClassificationAndStatus(Model, ElementStatus.FINALIZED),
                totalDataSetCount:countWithClassification(Classification),
                pendingActionCount:Action.countByState(ActionState.PENDING),
                failedActionCount:Action.countByState(ActionState.FAILED),
                activeBatchCount:Batch.countByArchived(false),
                archivedBatchCount:Batch.countByArchived(true),
                uninstantiatedDataElementCount: uninstantiatedDataElements.total,
                relationshipTypeCount:RelationshipType.count(),
                measurementUnitCount: countWithClassificationAndStatus(MeasurementUnit, ElementStatus.FINALIZED),
                dataTypeCount: countWithClassificationAndStatus(DataType, ElementStatus.FINALIZED),
                valueDomainCount:countWithClassification(ValueDomain),
                incompleteValueDomainsCount: dataArchitectService.incompleteValueDomains(params).total,
                transformationsCount:CsvTransformation.count()
                ]
        respond model
    }

    private Long countWithClassification(Class resource) {
        classificationService.classified(Lists.all([:], resource)).total
    }

    private Long countWithClassificationAndStatus(Class resource, ElementStatus desiredStatus) {
        classificationService.classified(resource).build {
            eq 'status', desiredStatus
        }.count()
    }
}

