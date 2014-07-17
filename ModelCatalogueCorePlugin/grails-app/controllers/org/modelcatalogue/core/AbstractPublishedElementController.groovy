package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.Lists

import static org.springframework.http.HttpStatus.OK

class AbstractPublishedElementController<T> extends AbstractExtendibleElementController<T> {

    def publishedElementService

    AbstractPublishedElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

    @Override
    def index(Integer max) {
        handleParams(max)

        respond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'status', PublishedElementService.getStatusFromParams(params)
        }
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    @Override
    @Transactional
    def update() {
        if(handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        def oldProps = new HashMap(instance.properties)

        oldProps.remove('modelCatalogueId')

        T helper = createResource(oldProps)

        def paramsToBind = getParametersToBind()
        def ext = paramsToBind.ext
        paramsToBind.remove 'ext'

        helper.properties = paramsToBind

        if (helper.hasErrors()) {
            respond helper.errors, view:'edit' // STATUS CODE 422
            return
        }

        if (params.boolean('newVersion')) {
            publishedElementService.archiveAndIncreaseVersion(instance)
        }

        if (ext != null) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        instance.properties = paramsToBind
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

    def history(Integer max){
        handleParams(max)
        PublishedElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        def customParams = [:]
        customParams.putAll params

        customParams.sort   = 'versionNumber'
        customParams.order  = 'desc'

        respond Lists.fromCriteria(customParams, resource, "/${resourceName}/${params.id}/history") {
            ilike 'modelCatalogueId', "$element.bareModelCatalogueId%"
        }
    }

}
