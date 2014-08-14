package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.Lists

import static org.springframework.http.HttpStatus.OK

class AbstractPublishedElementController<T> extends AbstractExtendibleElementController<T> {

    def publishedElementService, relationshipTypeService

    AbstractPublishedElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

    @Override
    def index(Integer max) {
        handleParams(max)

        reportCapableRespond Lists.fromCriteria(params, resource, "/${resourceName}/") {
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

//        T helper = resource.newInstance()

        T helper = createResource(oldProps)


        def relationshipDirections = relationshipTypeService.getRelationshipTypesFor(resource).collect{it.value}.collectMany {[RelationshipType.toCamelCase(it.sourceToDestination), RelationshipType.toCamelCase(it.destinationToSource)]}
        def excludeParams = ['ext', 'versionCreated', 'modelCatalogueId', 'outgoingRelations', 'incomingRelations']
        excludeParams.addAll(relationshipDirections)
//        def paramsToBind = getParametersToBind()
//        def ext = paramsToBind.ext
//        paramsToBind.remove 'ext'
//        paramsToBind.remove 'versionCreated'

        if (params.boolean('newVersion')) {
            excludeParams.add('status')
//            paramsToBind.remove 'status'
        }

//        helper.properties = paramsToBind
        bindData(helper, getObjectToBind(), [exclude: excludeParams])


        if (helper.hasErrors()) {
            reportCapableRespond helper.errors, view:'edit' // STATUS CODE 422
            return
        }

        if (params.boolean('newVersion')) {
            publishedElementService.archiveAndIncreaseVersion(instance)
        }

        if (params.ext != null) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        bindData(instance, getObjectToBind(), [exclude: excludeParams])
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

    def history(Integer max){
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
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

        reportCapableRespond Lists.fromCriteria(customParams, resource, "/${resourceName}/${params.id}/history") {
            ilike 'modelCatalogueId', "$element.bareModelCatalogueId%"
        }
    }

}
