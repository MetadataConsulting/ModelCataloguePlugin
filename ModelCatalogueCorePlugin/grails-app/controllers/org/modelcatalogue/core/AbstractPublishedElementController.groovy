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

        def newVersion = Boolean.valueOf(params?.newVersion)
        def ext = params?.ext
        def oldProps = new HashMap(instance.properties)
        oldProps.remove('modelCatalogueId')

//        T helper = resource.newInstance()

        T helper = createResource(oldProps)

//
//        def relationshipDirections = relationshipTypeService.getRelationshipTypesFor(resource).collect{it.value}.collectMany {[RelationshipType.toCamelCase(it.sourceToDestination), RelationshipType.toCamelCase(it.destinationToSource)]}
//        def excludeParams = ['ext', 'versionCreated', 'modelCatalogueId', 'outgoingRelations', 'incomingRelations','elementType', 'elementTypes', 'elementTypeName', 'dateCreated', 'lastUpdated', 'link', 'availableReports', 'defaultExcludes', 'updatableProperties', '__enhancedBy']
//        excludeParams.addAll(relationshipDirections)
//        def paramsToBind = getParametersToBind()
//        def ext = paramsToBind.ext
//        paramsToBind.remove 'ext'
//        paramsToBind.remove 'versionCreated'



        def includeParams = includeFields


        switch(response.format){

            case "json":
                if(!newVersion) newVersion = (request.JSON?.newVersion)?request.JSON?.newVersion.toBoolean():false
                if(!ext) ext = request.JSON?.ext
                break

            case "xml":
                if(!newVersion) newVersion = (request.XML?.newVersion)?request.XML?.newVersion.toBoolean():false
                if(!ext) ext = request.XML?.ext
                break

            default:
                newVersion = false
                break

        }

        if (newVersion) includeParams.remove('status')

//        if (newVersion) excludeParams.add('status')
//            paramsToBind.remove 'status'


//        helper.properties = paramsToBind
        bindData(helper, getObjectToBind(), [include: includeParams])


        if (helper.hasErrors()) {
            reportCapableRespond helper.errors, view:'edit' // STATUS CODE 422
            return
        }



        if (newVersion) {
            publishedElementService.archiveAndIncreaseVersion(instance)
        }

        if (ext) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        bindData(instance, getObjectToBind(), [include: includeParams])
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

    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['versionCreated', 'versionNumber'])
        fields
    }

}
