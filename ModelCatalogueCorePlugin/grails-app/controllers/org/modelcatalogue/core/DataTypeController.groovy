package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class DataTypeController<T> extends AbstractPublishedElementController<DataType> {

    DataTypeController() {
        super(DataType, false)
    }

    DataTypeController(Class<? extends DataType> resource) {
        super(resource, false)
    }


    def valueDomains(Integer max){
        handleParams(max)
        DataType dataType = queryForResource(params.id)
        if (!dataType) {
            notFound()
            return
        }

        respond classificationService.classified(Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain") {
            eq "dataType", dataType
//            if (!dataType.attach().archived) {
//                ne 'status', ElementStatus.DEPRECATED
//                ne 'status', ElementStatus.UPDATED
//                ne 'status', ElementStatus.REMOVED
//            }
        })

    }

}
