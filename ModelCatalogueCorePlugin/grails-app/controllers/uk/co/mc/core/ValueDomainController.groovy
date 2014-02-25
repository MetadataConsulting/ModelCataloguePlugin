package uk.co.mc.core

import uk.co.mc.core.util.Mappings

class ValueDomainController extends CatalogueElementController<ValueDomain> {

    ValueDomainController() {
        super(ValueDomain)
    }

    def mappings(Integer max){
        params.max = Math.min(max ?: 10, 100)
        ValueDomain domain = queryForResource(params.id)
        if (!domain) {
            notFound()
            return
        }

        int total = domain.outgoingMappings.size()
        def list = Mapping.findAllBySource(domain, params)
        def links = nextAndPreviousLinks("/${resourceName}/mapping/${params.id}", total)

        respond new Mappings(
                items: list,
                previous: links.previous,
                next: links.next,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }

}
