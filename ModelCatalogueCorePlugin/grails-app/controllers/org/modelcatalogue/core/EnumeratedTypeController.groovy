package org.modelcatalogue.core

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import static org.springframework.http.HttpStatus.OK

//TODO implement custom resource binding for xml enumerated type
//at present we clear the errors

class EnumeratedTypeController extends DataTypeController<EnumeratedType> {

    EnumeratedTypeController() {
        super(EnumeratedType)
    }

    @Override
    protected EnumeratedType createResource(Map params) {

        def json = request.getJSON()
        if(json){
            def instance = super.createResource(json)
            return instance
        }

        def xml = request.getXML()
        if(xml){
            EnumeratedType instance = super.createResource(params)
            //TODO implement
            instance.clearErrors()
            instance.enumerations = getXMLEnumerations(xml)
            instance.save()
            return instance
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


        if(request.format == "json"){
            instance.properties = request.getJSON()
        }else{
            instance.properties = getParametersToBind()
        }

        if(request.format == "xml"){
            instance.clearErrors()
            def xml = request.getXML()
            instance.enumerations = getXMLEnumerations(xml)
            instance.save()
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
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                reportCapableRespond instance, [status: OK]
            }
        }
    }

}
