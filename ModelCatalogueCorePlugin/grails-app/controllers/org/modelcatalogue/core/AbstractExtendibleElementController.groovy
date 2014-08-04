package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import static org.springframework.http.HttpStatus.OK

class AbstractExtendibleElementController<T> extends AbstractCatalogueElementController<T> {

    AbstractExtendibleElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    @Transactional
    def update() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

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
            reportCapableRespond helper.errors, view:'edit' // STATUS CODE 422
            return
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
                reportCapableRespond instance, [status: OK]
            }
        }
    }

}
