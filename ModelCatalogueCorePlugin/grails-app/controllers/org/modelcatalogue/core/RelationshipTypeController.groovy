package org.modelcatalogue.core

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.CatalogueElementFinder
import org.modelcatalogue.core.util.Lists

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.OK

class RelationshipTypeController extends AbstractRestfulController<RelationshipType>{

    RelationshipTypeController() {
        super(RelationshipType)
    }

    @Override
    def index(Integer max) {
        handleParams(max)
        reportCapableRespond Lists.fromCriteria(params, resource, "/${resourceName}/") {
            if (!params.boolean('system')) {
                eq 'system', false
            }
        }
    }

    def elementClasses() {
        reportCapableRespond CatalogueElementFinder.catalogueElementClasses
    }

    @Override
    protected RelationshipType createResource(Map params) {

        def json = request.getJSON()
        if(json){
            def src = (json?.sourceClass)?this.class.classLoader.loadClass(json.sourceClass):null
            def dest = (json?.destinationClass)?this.class.classLoader.loadClass(json.destinationClass):null
            def instance = new RelationshipType(name: json?.name, sourceClass: src, destinationClass: dest, sourceToDestination: json?.sourceToDestination, destinationToSource: json?.destinationToSource)
            return instance
        }

        def xml = request.getXML()
        if(xml){
            def src = (xml?.sourceClass?.toString())?this.class.classLoader.loadClass(xml.sourceClass.toString()):null
            def dest = (xml?.destinationClass?.toString())?this.class.classLoader.loadClass(xml.destinationClass.toString()):null
            def instance = new RelationshipType(name: xml?.name?.toString(), sourceClass: src, destinationClass: dest, sourceToDestination: xml?.sourceToDestination?.toString(), destinationToSource: xml?.destinationToSource?.toString())
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

        RelationshipType instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        if(request.format == "json"){
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
        }else if(request.format == "xml"){
            GPathResult xml = request.getXML()
            Map props = [:]
            def name = xml.getProperty("name").toString()
            if(name){props.put("name", name)}
            def sourceToDestination = xml.getProperty("sourceToDestination").toString()
            if(sourceToDestination){props.put("sourceToDestination", sourceToDestination)}
            def destinationToSource = xml.getProperty("destinationToSource").toString()
            if(destinationToSource){props.put("destinationToSource", destinationToSource)}
            def src = xml.getProperty("sourceClass").toString()
            if(src){
                try{
                    props.sourceClass = this.class.classLoader.loadClass(src)
                }catch(ClassNotFoundException ignored){
                    instance.errors.rejectValue("sourceClass", "org.modelcatalogue.core.RelationshipType.sourceClass.domainNotFound")
                }
            }
            def dest = xml.getProperty("destinationClass").toString()
            if(dest){
                try{
                    props.destinationClass = this.class.classLoader.loadClass(dest)
                }catch(ClassNotFoundException ignored){
                    instance.errors.rejectValue("destinationClass", "org.modelcatalogue.core.RelationshipType.destinationClass.domainNotFound")
                }
            }
            instance.properties = props
        }else{
            instance.properties = getParametersToBind()
        }

        if (instance.hasErrors()) {
            reportCapableRespond instance.errors, view:'edit' // STATUS CODE 422
            return
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
                                namespace: hasProperty('namespace') ? this.namespace : null ).toString())
                reportCapableRespond instance, [status: OK]
            }
        }
    }


}
