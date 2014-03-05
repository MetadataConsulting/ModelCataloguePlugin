package org.modelcatalogue.core

import org.modelcatalogue.core.util.Mappings

import javax.servlet.http.HttpServletResponse

class ValueDomainController extends CatalogueElementController<ValueDomain> {

    static allowedMethods = [mappings: "GET", removoMapping: "DELETE", addMapping: "POST"]

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
        def links = nextAndPreviousLinks("/${resourceName}/${params.id}/mapping", total)

        respond new Mappings(
                items: list,
                previous: links.previous,
                next: links.next,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }

    def removeMapping() {
        addOrRemoveMapping(false)
    }

    def addMapping() {
        addOrRemoveMapping(true)
    }

    private addOrRemoveMapping(boolean add) {
        if (!params.destination || !params.id) {
            notFound()
            return
        }
        ValueDomain domain = queryForResource(params.id)
        if (!domain) {
            notFound()
            return
        }

        ValueDomain destination = queryForResource(params.destination)
        if (!destination) {
            notFound()
            return
        }
        if (add) {
            String mappingString = null
            withFormat {
                xml {
                    mappingString = request.getXML().text()
                }
                json {
                    mappingString = request.getJSON().mapping
                }
            }
            Mapping mapping = Mapping.map(domain, destination, mappingString)
            if (mapping.hasErrors()) {
                respond mapping.errors
                return
            }
            response.status = HttpServletResponse.SC_CREATED
            respond mapping
            return
        }
        Mapping old = Mapping.unmap(domain, destination)
        if (old) {
            response.status = HttpServletResponse.SC_NO_CONTENT
        } else {
            notFound()
        }
    }

}
