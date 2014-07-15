package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.modelcatalogue.core.util.DetachedListWrapper
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.SimpleListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.NO_CONTENT

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
        handleParams(max)
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

    protected handleParams(Integer max) {
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
        if (defaultSort && !params.sort)    params.sort     = defaultSort
        if (defaultOrder && !params.order)  params.order    = defaultOrder
    }

    @Override
    def index(Integer max) {
        handleParams(max)
        respond DetachedListWrapper.create(params, resource, basePath)
    }


    protected getBasePath()     { "/${resourceName}/" }
    protected getDefaultSort()  { null }
    protected getDefaultOrder() { null }


    def validate() {
        if(handleReadOnly()) {
            return
        }
        def instance = createResource(getParametersToBind())

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


    protected Map getParametersToBind() {
        Map ret = params
        if (response.format == 'json') {
            ret = request.getJSON()
        }
        ret
    }



    /**
     * @deprecated use DetachedListWrapper instead where possible
     */
    @Deprecated
    protected void respondWithLinks(Class itemType = resource, ListWrapper listWrapper) {
        def links = SimpleListWrapper.nextAndPreviousLinks(params, listWrapper.base, listWrapper.total)
        listWrapper.previous    = links.previous
        listWrapper.next        = links.next
        listWrapper.offset      = params.int('offset') ?: 0
        listWrapper.page        = params.int('max') ?: 10
        listWrapper.sort        = params.sort ?: defaultSort
        listWrapper.order       = params.order ?: defaultOrder
        if (!listWrapper.itemType) {
            listWrapper.itemType = itemType
        }
        respond listWrapper
    }

    /**
     * @deprecated This method is no longer used by index action
     */
    @Override @Deprecated
    protected List<T> listAllResources(Map params) {
        return super.listAllResources(params)
    }

    /**
     * @deprecated This method is no longer used by index action
     */
    @Override @Deprecated
    protected Integer countResources() {
        return super.countResources()
    }
}
