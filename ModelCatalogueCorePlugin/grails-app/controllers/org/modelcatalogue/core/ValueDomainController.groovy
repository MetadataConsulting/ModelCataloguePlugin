package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ValueDomainController extends AbstractCatalogueElementController<ValueDomain> {

    def dataArchitectService

    ValueDomainController() {
        super(ValueDomain, false)
    }

    @Override
    def index(Integer max) {
        if (params.status == 'incomplete') {
            handleParams(max)
            respond Lists.wrap(params, resource, basePath, dataArchitectService.incompleteValueDomains(params))
            return
        }
        if (params.status == 'duplicate') {
            handleParams(max)
            respond Lists.wrap(params, resource, basePath, dataArchitectService.duplicateValueDomains(params))
            return
        }
        if (params.status == 'unused') {
            handleParams(max)
            respond Lists.wrap(params, resource, basePath, dataArchitectService.unusedValueDomains(params))
            return
        }
        if(params.status && params.status.toLowerCase() != 'finalized' && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }
        super.index(max)
    }

    def dataElements(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        ValueDomain valueDomain = queryForResource(params.id)
        if (!valueDomain) {
            notFound()
            return
        }

        respond classificationService.classified(Lists.fromCriteria(params, DataElement, "/${resourceName}/${params.id}/dataElement") {
            eq "valueDomain", valueDomain
            if (!all && !valueDomain.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

    def convert() {
        ValueDomain valueDomain = queryForResource(params.id)
        if (!valueDomain) {
            notFound()
            return
        }

        ValueDomain other = queryForResource(params.destination)
        if (!other) {
            notFound()
            return
        }

        Mapping mapping = Mapping.findBySourceAndDestination(valueDomain, other)
        if (!mapping) {
            respond result: "Mapping is missing. Don't know how to convert value."
            return
        }

        if (!params.value) {
            respond result: "Please, enter value."
            return
        }

        def valid = valueDomain.validateRule(params.value)

        if (!(valid instanceof Boolean && valid)) {
            respond result: "INVALID: Please, enter valid value"
            return
        }

        def result = mapping.map(params.value)

        if (result instanceof Exception) {
            respond result: "ERROR: ${result.class.simpleName}: $result.message"
            return
        }

        respond result: result
    }


    def validateValue() {
        ValueDomain valueDomain = queryForResource(params.id)
        if (!valueDomain) {
            notFound()
            return
        }

        if (!valueDomain.rule && !(valueDomain.dataType?.instanceOf(EnumeratedType)) && valueDomain.countIsBasedOn() == 0) {
            respond result: "Don't know how to validate value."
            return
        }

        if (!params.value) {
            respond result: "Please, enter value."
            return
        }

        def result = valueDomain.validateRule(params.value)

        if (result instanceof Exception) {
            respond result: "ERROR: ${result.class.simpleName}: $result.message"
        }

        respond result: result
    }
}
