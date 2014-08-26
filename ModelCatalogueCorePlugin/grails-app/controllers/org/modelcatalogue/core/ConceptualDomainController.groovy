package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ConceptualDomainController extends AbstractCatalogueElementController<ConceptualDomain> {

    ConceptualDomainController() {
        super(ConceptualDomain)
    }

    def valueDomains(Integer max){
        handleParams(max)

        ConceptualDomain conceptualDomain = queryForResource(params.id)
        if (!conceptualDomain) {
            notFound()
            return
        }

        reportCapableRespond Lists.fromCriteria(params, ValueDomain, "/${resourceName}/${params.id}/valueDomain", "valueDomain"){
            conceptualDomains {
                eq 'id', conceptualDomain.id
            }
        }

    }



}
