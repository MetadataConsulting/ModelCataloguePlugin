package org.modelcatalogue.core

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import static org.springframework.http.HttpStatus.OK


class EnumeratedTypeController extends DataTypeController<EnumeratedType> {

    EnumeratedTypeController() {
        super(EnumeratedType)
    }

    @Override
    protected EnumeratedType createResource() {
        EnumeratedType instance = resource.newInstance()
        def relationshipDirections = relationshipTypeService.getRelationshipTypesFor(resource).collect{it.value}.collectMany {[RelationshipType.toCamelCase(it.sourceToDestination), RelationshipType.toCamelCase(it.destinationToSource)]}
        def excludeParams = ['ext', 'outgoingRelations', 'incomingRelations']
        excludeParams.addAll(relationshipDirections)

        if(request.format=='xml'){
            excludeParams.add('enumerations')
            bindData instance, getObjectToBind(), [exclude: excludeParams]
            instance.enumerations = getXMLEnumerations(request.getXML())
            instance.save()
            instance
        }else{
            bindData instance, getObjectToBind(), [exclude: excludeParams]
            instance
        }
    }

    private static Map getXMLEnumerations(GPathResult xml){
        def xmlEnumerations = xml.depthFirst().find { it.name() == 'enumerations' }
        Map propMap = [:]
        xmlEnumerations.enumeration.each{
            propMap.put(it.@key.toString(), it.text())
        }
        return propMap
    }


    /**
     * Updates a resource for the given id
     * @param id
     */

    //NB: Almost the same method as the abstract restful controller - added an extra line when updating XML enumerations

    @Transactional
    @Override
    def update() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        if(handleReadOnly()) {
            return
        }

        EnumeratedType instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }


        def relationshipDirections = relationshipTypeService.getRelationshipTypesFor(resource).collect{it.value}.collectMany {[RelationshipType.toCamelCase(it.sourceToDestination), RelationshipType.toCamelCase(it.destinationToSource)]}
        def excludeParams = ['ext', 'classifiedName', 'outgoingRelations', 'incomingRelations', 'dateCreated', 'lastUpdated', 'archived']
        excludeParams.addAll(relationshipDirections)

        if(request.format == "xml"){
            excludeParams.add('enumerations')
            bindData(instance, getObjectToBind(), [exclude: excludeParams])
            instance.enumerations = getXMLEnumerations(request.getXML())
            instance.save()
        }else{
            bindData(instance, getObjectToBind(), [exclude: excludeParams])
        }

        if (instance.hasErrors()) {
            reportCapableRespond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        def ext = params?.ext
        switch(response.format){

            case "json":
                if(!ext) ext = request.JSON?.ext
                break

            case "xml":
                if(!ext) ext = request.XML?.ext
                break

            default:
                break

        }
        if (ext != null) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        instance.save flush:true


        request.withFormat {
            form {
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
