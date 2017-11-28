package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.OK
import grails.transaction.Transactional
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.util.CatalogueElementFinder
import org.modelcatalogue.core.util.lists.Lists

import javax.servlet.http.HttpServletResponse

class RelationshipTypeController extends AbstractRestfulController<RelationshipType>{

    RelationshipTypeGormService relationshipTypeGormService

    RelationshipTypeController() {
        super(RelationshipType)
    }

    protected RelationshipType findById(long id) {
        relationshipTypeGormService.findById(id)
    }

    @Override
    def index(Integer max) {
        handleParams(max)
        respond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            if (!params.boolean('system')) {
                eq 'system', false
            }
        }
    }

    def elementClasses() {
        respond CatalogueElementFinder.catalogueElementClasses
    }

    @Override
    protected RelationshipType createResource() {

        def json = request.getJSON()
        if(json){
            def src = (json?.sourceClass)?this.class.classLoader.loadClass(json.sourceClass):null
            def dest = (json?.destinationClass)?this.class.classLoader.loadClass(json.destinationClass):null
            def instance = new RelationshipType(name: json?.name, sourceClass: src, destinationClass: dest, sourceToDestination: json?.sourceToDestination, destinationToSource: json?.destinationToSource)
            return instance
        }

        response.status = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE
        return null
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

        RelationshipType instance = findById(params.long('id'))
        if (instance == null) {
            notFound()
            return
        }

        def json = request.getJSON()
        Map props = json
        def src = json?.sourceClass?.toString()
        if(src){
            try{
                props.sourceClass = this.class.classLoader.loadClass(src)
            }catch(ClassNotFoundException ignored){
                instance.errors.rejectValue("sourceClass", "org.modelcatalogue.core.RelationshipType.sourceClass.domainNotFound")
            }
        }
        def dest = json?.destinationClass?.toString()
        if(dest){
            try{
                props.destinationClass = this.class.classLoader.loadClass(dest)
            }catch(ClassNotFoundException ignored){
                instance.errors.rejectValue("destinationClass", "org.modelcatalogue.core.RelationshipType.destinationClass.domainNotFound")
            }
        }
        instance.properties = props


        if (instance.hasErrors()) {
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        instance.save flush:true
        respond instance, [status: OK]
    }
}
