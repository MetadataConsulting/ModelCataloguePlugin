package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.lists.Lists

class MeasurementUnitController extends AbstractCatalogueElementController<MeasurementUnit> {

    MeasurementUnitController() {
        super(MeasurementUnit, false)
    }

    def primitiveTypes(Integer max){
        handleParams(max)

        MeasurementUnit unit = queryForResource(params.id)
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

    @Override
    protected boolean hasUniqueName() {
        true
    }


}
