package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.dataarchitect.DataImport

class DashboardController {

    static responseFormats = ['json', 'xml', 'xlsx']
    def dataArchitectService
    def modelCatalogueSecurityService

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
                unusedValueDomainsCount: dataArchitectService.unusedValueDomains(params).total,
                // duplicateValueDomainsCount: dataArchitectService.duplicateValueDomains(params).total,
                conceptualDomainCount:countWithClassification(ConceptualDomain),
                transformationsCount:CsvTransformation.count(),
                importCount:DataImport.count(),
                ]
        respond model
    }

    Long countWithClassification(Class resource) {
        List<Classification> classificationsInUse = modelCatalogueSecurityService.currentUser?.classifications ?: []

        if (!classificationsInUse) {
            return resource.count()
        }

        DetachedCriteria criteria = new DetachedCriteria(resource)
        criteria.build {
            incomingRelationships {
                'eq' 'relationshipType', RelationshipType.classificationType
                'in' 'source', classificationsInUse
            }
        }.count()
    }

    Long countWithClassificationAndStatus(Class resource, ElementStatus desiredStatus) {
        List<Classification> classificationsInUse = modelCatalogueSecurityService.currentUser?.classifications ?: []

        if (!classificationsInUse) {
            return resource.countByStatus(desiredStatus)
        }

        DetachedCriteria criteria = new DetachedCriteria(resource)
        criteria.build {
            eq 'status', desiredStatus
            incomingRelationships {
                'eq' 'relationshipType', RelationshipType.classificationType
                'in' 'source', classificationsInUse
            }
        }.count()
    }
}

