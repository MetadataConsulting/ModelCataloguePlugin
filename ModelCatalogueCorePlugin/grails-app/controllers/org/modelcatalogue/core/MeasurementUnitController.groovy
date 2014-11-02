package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class MeasurementUnitController extends AbstractPublishedElementController<MeasurementUnit> {

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

        reportCapableRespond classificationService.classified(Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain", "valueDomains"){
            eq "unitOfMeasure", unit
        })

    }


}
