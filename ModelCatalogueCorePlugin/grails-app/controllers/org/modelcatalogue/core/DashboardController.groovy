package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
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
        response.addHeader('Expires', '-1')
        def uninstantiatedDataElements = dataArchitectService.uninstantiatedDataElements(params)

        def model = [:]

        List<Class> displayed = [Classification, Model, DataElement, ValueDomain, DataType, MeasurementUnit, Asset]

        for (Class type in displayed) {
            DetachedCriteria criteria = classificationService.classified(type)
            criteria.projections {
                property 'status'
                property 'id'
            }
            criteria.inList('status', [ElementStatus.DRAFT, ElementStatus.FINALIZED])


            int draft = 0
            int finalized = 0

            for (Object[] row in criteria.list()) {
                ElementStatus status = row[0] as ElementStatus
                if (status == ElementStatus.DRAFT) draft++
                else if (status == ElementStatus.FINALIZED) finalized++
            }

            model["draft${type.simpleName}Count"]       = draft
            model["finalized${type.simpleName}Count"]   = finalized
            model["total${type.simpleName}Count"]       = draft + finalized
        }

        model.putAll([
            activeBatchCount:Batch.countByArchived(false),
            archivedBatchCount:Batch.countByArchived(true),
            uninstantiatedDataElementCount: uninstantiatedDataElements.total,
            relationshipTypeCount:RelationshipType.count(),
            incompleteValueDomainsCount: dataArchitectService.incompleteValueDomains(params).total,
            transformationsCount:CsvTransformation.count()
        ])
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

