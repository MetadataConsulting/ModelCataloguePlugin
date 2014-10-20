package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class MeasurementUnitController extends AbstractCatalogueElementController<MeasurementUnit> {

    MeasurementUnitController() {
        super(MeasurementUnit)
    }

    def valueDomains(Integer max){
        handleParams(max)

        MeasurementUnit unit = queryForResource(params.id)
        if (!unit) {
            notFound()
            return
        }

        reportCapableRespond Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain", "valueDomains"){
            eq "unitOfMeasure", unit
        }

    }


}
