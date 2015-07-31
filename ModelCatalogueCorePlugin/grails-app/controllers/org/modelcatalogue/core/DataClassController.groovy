package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.Lists

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    def dataClassService

    DataClassController() {
        super(DataClass, false)
    }

    @Override
    def index(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.index(max)
        }
        if(params.status && params.status.toLowerCase() != 'finalized' && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }
        handleParams(max)

        respond Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(params))
    }

    def referenceTypes(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        DataClass dataClass = queryForResource(params.id)
        if (!dataClass) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, ReferenceType, "/${resourceName}/${params.id}/referenceType") {
            eq "dataClass", dataClass
            if (!all && !dataClass.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

}
