package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.Elements

import static org.springframework.http.HttpStatus.OK

class AbstractPublishedElementController<T> extends AbstractExtendibleElementController<T> {

    def publishedElementService

    AbstractPublishedElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

    @Override
    def index(Integer max) {
        setSafeMax(max)
        Integer total = publishedElementService.count(params, resource)
        def list = publishedElementService.list(params, resource)

        respondWithLinks new Elements(
                base: "/${resourceName}/",
                total: total,
                items: list
        )
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
        setSafeMax(max)
        PublishedElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        def customParams = [:]
        customParams.putAll params

        customParams.sort   = 'versionNumber'
        customParams.order  = 'desc'

        int total = resource.countByModelCatalogueIdLike "$element.bareModelCatalogueId%"
        def list = resource.findAllByModelCatalogueIdLike "$element.bareModelCatalogueId%", customParams

        respondWithLinks new Elements(
                base: "/${resourceName}/${params.id}/history",
                items: list,
                total: total
        )
    }



    protected Map getParametersToBind() {
        Map ret = super.parametersToBind
        ret.remove 'modelCatalogueId'
        ret.remove 'versionNumber'
        ret
    }

}
