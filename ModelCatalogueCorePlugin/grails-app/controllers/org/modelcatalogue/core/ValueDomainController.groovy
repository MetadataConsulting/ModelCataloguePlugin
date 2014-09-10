package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ValueDomainController extends AbstractExtendibleElementController<ValueDomain> {

    def dataArchitectService

    ValueDomainController() {
        super(ValueDomain, false)
    }

    @Override
    def index(Integer max) {
        if (params.status == 'incomplete') {
            handleParams(max)
            reportCapableRespond Lists.wrap(params, resource, basePath, dataArchitectService.incompleteValueDomains(params))
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

        reportCapableRespond Lists.fromCriteria(params, DataElement, "/${resourceName}/${params.id}/dataElement", "dataElements"){
            eq "valueDomain", valueDomain
            if (!all) {
                ne 'status', PublishedElementStatus.ARCHIVED
                ne 'status', PublishedElementStatus.UPDATED
                ne 'status', PublishedElementStatus.REMOVED
            }
        }

    }

    // conceptual domains are marshalled with the value domain so no need for special method to fetch them

    protected bindRelations(ValueDomain instance) {
        if (objectToBind.conceptualDomains != null) {
            for (domain in instance.conceptualDomains.findAll { !(it.id in objectToBind.conceptualDomains*.id) }) {
                instance.removeFromConceptualDomains(domain)
                domain.removeFromValueDomains(instance)
            }
            for (domain in objectToBind.conceptualDomains) {
                ConceptualDomain conceptualDomain = ConceptualDomain.get(domain.id as Long)
                instance.addToConceptualDomains conceptualDomain
                conceptualDomain.addToValueDomains instance
            }
        }
    }

    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['conceptualDomains'])
        fields
    }
}
