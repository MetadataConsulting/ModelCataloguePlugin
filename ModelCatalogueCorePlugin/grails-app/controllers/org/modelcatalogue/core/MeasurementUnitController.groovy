package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class MeasurementUnitController extends AbstractCatalogueElementController<MeasurementUnit> {

    MeasurementUnitController() {
        super(MeasurementUnit, false)
    }

    def valueDomains(Integer max){
        handleParams(max)

        MeasurementUnit unit = queryForResource(params.id)
        if (!unit) {
            notFound()
            return
        }

        respond classificationService.classified(Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain") {
            eq "unitOfMeasure", unit
            if (!unit.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }


}
