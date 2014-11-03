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
        bindData instance, getObjectToBind(), [exclude: excludeParams]
        instance
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

        bindData(instance, getObjectToBind(), [exclude: excludeParams])

        if (instance.hasErrors()) {
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        def ext = request.JSON?.ext

        if (ext != null) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        instance.save flush:true

        respond instance, [status: OK]
    }

}
