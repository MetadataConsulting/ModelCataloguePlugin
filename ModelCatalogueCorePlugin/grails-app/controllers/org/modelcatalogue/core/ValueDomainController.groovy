package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ValueDomainController extends AbstractExtendibleElementController<ValueDomain> {

    ValueDomainController() {
        super(ValueDomain, false)
    }


    def dataElements(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        ValueDomain valueDomain = queryForResource(params.id)
        if (!valueDomain) {
            notFound()
            return
        }

        reportCapableRespond Lists.fromCriteria(params, DataElement, "/${resourceName}/${params.id}/dataElement", "dataElements"){
            eq "valueDomain", valueDomain
            if (!all) {
                'in'('status', [PublishedElementStatus.FINALIZED, PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING])
            }
        }

    }

}
