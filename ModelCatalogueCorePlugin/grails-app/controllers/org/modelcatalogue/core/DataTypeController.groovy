package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.ValueDomains

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

        reportCapableRespond new ValueDomains(list: classificationService.classified(Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain", "valueDomains"){
            eq "dataType", dataType
        }))

    }

}
