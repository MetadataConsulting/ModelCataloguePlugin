package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.modelcatalogue.core.util.Elements
import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.NO_CONTENT

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml']
    def searchService

    AbstractRestfulController(Class<T> resource) {
        super(resource)
    }

    def search(Integer max){
        params.max = Math.min(max ?: 10, 100)
        def results =  searchService.search(resource, params)

        if(results.errors){
            respond results
            return
        }

        def total = (results.total)?results.total.intValue():0
        def links = nextAndPreviousLinks("/${resourceName}/search", total)
            respond new Elements(
                    total: total,
                    items: results.searchResults,
                    previous: links.previous,
                    next: links.next,
                    offset: params.int('offset') ?: 0,
                    page: params.int('max') ?: 10
            )

    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def total = countResources()
        def list = listAllResources(params)
        def links = nextAndPreviousLinks("/${resourceName}/", total)
        respond new Elements(
                total: total,
                items: list,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }


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

    protected Map<String, String> nextAndPreviousLinks(String baseLink, Integer total) {
        def link = "${baseLink}?"
        if (params.max) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.order) {
            link += "&order=${params.order}"
        }
        if (params.search){
            link +=  "&search=${params.search}"
        }
        def nextLink = ""
        def previousLink = ""
        if (params?.max && params.max < total) {
            def offset = (params?.offset) ? params?.offset?.toInteger() : 0
            def prev = offset - params?.max
            def next = offset + params?.max
            if (next < total) {
                nextLink = "${link}&offset=${next}"
            }
            if (prev >= 0) {
                previousLink = "${link}&offset=${prev}"
            }
        }
        [
                next: nextLink,
                previous: previousLink
        ]
    }

}
