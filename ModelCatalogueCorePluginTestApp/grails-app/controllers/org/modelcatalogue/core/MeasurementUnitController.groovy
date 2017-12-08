package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.catalogueelement.MeasurementUnitCatalogueElementService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.util.lists.Lists

class MeasurementUnitController extends AbstractCatalogueElementController<MeasurementUnit> {

    MeasurementUnitGormService measurementUnitGormService

    MeasurementUnitCatalogueElementService measurementUnitCatalogueElementService

    MeasurementUnitController() {
        super(MeasurementUnit, false)
    }

    def primitiveTypes(Integer max){
        handleParams(max)

        MeasurementUnit unit = findById(params.long('id'))
        if (!unit) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, PrimitiveType, "/${resourceName}/${params.id}/primitiveType") {
            eq "measurementUnit", unit
            if (!unit.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

    protected MeasurementUnit findById(long id) {
        measurementUnitGormService.findById(id)
    }

    @Override
    protected boolean hasUniqueName() {
        true
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        measurementUnitCatalogueElementService
    }

}
