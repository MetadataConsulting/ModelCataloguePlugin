package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.Lists

class DashboardController {

    static responseFormats = ['json', 'xlsx']

    def dataModelService

    def index() {
        response.addHeader('Expires', '-1')

        def model = [:]

        List<Class> displayed = [DataModel, DataClass, DataElement, DataType, MeasurementUnit, Asset]

        for (Class type in displayed) {
            DetachedCriteria criteria = dataModelService.classified(type, overridableDataModelFilter)
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
            relationshipTypeCount:RelationshipType.count(),
            transformationsCount:CsvTransformation.count()
        ])
        respond model
    }

    protected DataModelFilter getOverridableDataModelFilter() {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }

}

