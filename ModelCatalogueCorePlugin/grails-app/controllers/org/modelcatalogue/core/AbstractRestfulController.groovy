package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']
    def modelCatalogueSearchService
    XLSXListRenderer xlsxListRenderer


    AbstractRestfulController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractRestfulController(Class<T> resource) {
        super(resource, false)
    }

    def search(Integer max){
        setSafeMax(max)
        def results =  modelCatalogueSearchService.search(resource, params)

        if(results.errors){
            respond results
            return
        }

        def total = (results.total)?results.total.intValue():0

        Elements elements = new Elements(
                base: "/${resourceName}/search",
                total: total,
                items: results.searchResults
            )
        respondWithLinks elements
    }

    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
            xml {
                params.max = Math.min(max ?: 10000, 10000)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }

    }

    @Override
    def index(Integer max) {
        setSafeMax(max)
        def total = countResources()
        def list = listAllResources(params)

        respondWithLinks new Elements(
                base: "/${resourceName}/",
                total: total,
                items: list
        )
    }

    protected getDefaultSort()  { null }
    protected getDefaultOrder() { null }


    def validate() {
        if(handleReadOnly()) {
            return
        }
        def instance = createResource()

        instance.validate()
        if (instance.hasErrors()) {
            respond instance.errors, view:'create' // STATUS CODE 422
            return
        }

        respond instance
    }

    @Override
    @Transactional
    def delete() {
        if(handleReadOnly()) {
            return
        }

        def instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        try{
            instance.delete flush:true
        }catch (DataIntegrityViolationException ignored){
            response.status = HttpServletResponse.SC_CONFLICT
            respond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"]) // STATUS CODE 409
            return
        } catch (Exception ignored){
            response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
            respond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])  // STATUS CODE 501
            return
        }

        render status: NO_CONTENT // NO CONTENT STATUS CODE
    }

    /**
     * Updates a resource for the given id
     * @param id
     * @deprecated this will be fixed in Groovy 2.4.1 (hopefully)
     */
    @Override
    @Transactional
    @Deprecated
    def update() {
        if(handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        instance.properties = request

        if (instance.hasErrors()) {
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        instance.save flush:true
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: "${resourceClassName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*'{
                response.addHeader(HttpHeaders.LOCATION,
                        g.createLink(
                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: OK]
            }
        }
    }



    protected void respondWithLinks(Class itemType = resource, ListWrapper listWrapper) {
        def links = ListWrapper.nextAndPreviousLinks(params, listWrapper.base, listWrapper.total)
        listWrapper.previous    = links.previous
        listWrapper.next        = links.next
        listWrapper.offset      = params.int('offset') ?: 0
        listWrapper.page        = params.int('max') ?: 0
        listWrapper.sort        = params.sort ?: defaultSort
        listWrapper.order       = params.order ?: defaultOrder
        if (!listWrapper.itemType) {
            listWrapper.itemType = itemType
        }
        respond listWrapper
    }

}
