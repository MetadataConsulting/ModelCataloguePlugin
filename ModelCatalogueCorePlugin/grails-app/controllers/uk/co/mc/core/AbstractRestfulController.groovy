package uk.co.mc.core

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import uk.co.mc.core.util.Elements

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.NO_CONTENT

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml']

    AbstractRestfulController(Class<T> resource) {
        super(resource)
    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def total = countResources()
        def list = listAllResources(params)
        def links = generateLinks("/${resourceName}/", total)
        respond new Elements(
                total: total,
                items: list,
                previous: links.previous,
                next: links.next,
                self: links.self,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
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
        }catch (DataIntegrityViolationException e){
            response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
            def model =  [errors: message(code: "uk.co.mc.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])] // STATUS CODE 501
            respond model
            return
        } catch (Exception e){
            def model =  [errors: message(code: "uk.co.mc.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])] // STATUS CODE 501
            respond model
            return
        }


        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: "${resourceClassName}.label".toString(), default: resourceClassName), instance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT } // NO CONTENT STATUS CODE
        }
    }

    protected Map<String, String> generateLinks(String baseLink, Integer total) {
        def link = "${baseLink}?"
        if (params.max && params.max != 10) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.order) {
            link += "&order=${params.order}"
        }
        def nextLink = ""
        def previousLink = ""
        def offset = (params?.offset) ? params?.offset?.toInteger() : 0
        if (params?.max && params.max < total) {
            def prev = offset - params?.max
            def next = offset + params?.max
            if (next < total) {
                nextLink = "${link}&offset=${next}"
            }
            if (prev >= 0) {
                if (prev == 0) {
                    previousLink = link
                } else {
                    previousLink = "${link}&offset=${prev}"
                }
            }
        }
        def selfLink = link
        if (offset != 0) {
            selfLink += "&offset=${offset}"
        }
        [
                next: cleanLink(nextLink),
                previous: cleanLink(previousLink),
                self: cleanLink(selfLink)
        ]
    }

    private static cleanLink(String link) {
        link = link.replace "?&", "?"
        if (link.endsWith("?")) link = link[0..-2]
        link
    }

}
