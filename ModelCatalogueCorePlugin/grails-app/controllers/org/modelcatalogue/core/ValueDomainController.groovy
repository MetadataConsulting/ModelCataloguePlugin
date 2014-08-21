package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ValueDomainController extends AbstractExtendibleElementController<ValueDomain> {

    ValueDomainController() {
        super(ValueDomain, false)
    }


    def valueDomains(Integer max){
        handleParams(max)
        DataType dataType = queryForResource(params.id)
        if (!dataType) {
            notFound()
            return
        }

        reportCapableRespond new DataElements(list: Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain", "valueDomains"){
            eq "dataType", dataType
        })

    }

}
