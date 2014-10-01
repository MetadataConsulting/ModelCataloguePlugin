package org.modelcatalogue.core

import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.dataarchitect.DataImport

class DashboardController {

    static responseFormats = ['json', 'xml', 'xlsx']
    def dataArchitectService

    def index() {

        def uninstantiatedDataElements = dataArchitectService.uninstantiatedDataElements(params)

        def model = [
                totalDataElementCount: DataElement.count(),
                draftDataElementCount:DataElement.countByStatus(PublishedElementStatus.DRAFT),
                finalizedDataElementCount:DataElement.countByStatus(PublishedElementStatus.FINALIZED),
                totalAssetCount: Asset.count(),
                draftAssetCount:Asset.countByStatus(PublishedElementStatus.DRAFT),
                finalizedAssetCount:Asset.countByStatus(PublishedElementStatus.FINALIZED),
                totalModelCount:Model.count(),
                draftModelCount:Model.countByStatus(PublishedElementStatus.DRAFT),
                finalizedModelCount:Model.countByStatus(PublishedElementStatus.FINALIZED),
                totalDataSetCount:Classification.count(),
                pendingActionCount:Action.countByState(ActionState.PENDING),
                failedActionCount:Action.countByState(ActionState.FAILED),
                activeBatchCount:Batch.countByArchived(false),
                archivedBatchCount:Batch.countByArchived(true),
                uninstantiatedDataElementCount: uninstantiatedDataElements.total,
                relationshipTypeCount:RelationshipType.count(),
                measurementUnitCount:MeasurementUnit.count(),
                dataTypeCount:DataType.count(),
                valueDomainCount:ValueDomain.count(),
                incompleteValueDomainsCount: dataArchitectService.incompleteValueDomains(params).total,
                unusedValueDomainsCount: dataArchitectService.unusedValueDomains(params).total,
                // duplicateValueDomainsCount: dataArchitectService.duplicateValueDomains(params).total,
                conceptualDomainCount:ConceptualDomain.count(),
                transformationsCount:CsvTransformation.count(),
                importCount:DataImport.count(),
                ]
        respond model
    }
}

