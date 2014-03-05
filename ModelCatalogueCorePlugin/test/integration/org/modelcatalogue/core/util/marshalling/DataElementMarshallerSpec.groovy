package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared

/**
 * Created by adammilward on 10/02/2014.
 */
class DataElementMarshallerSpec extends AbstractIntegrationSpec{

    @Shared
    def de1, de2, de3, rel, rt


    def setupSpec(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        loadFixtures()

        de1 = DataElement.findByName("auth7")
        de2 = DataElement.findByName("auth7")
        de3 = DataElement.findByName("auth7")
        rt = RelationshipType.findByName("Synonym")

    }

    /*
    def cleanupSpec(){
        de1.delete()
        de2.delete()
        de3.delete()
        rt.delete()
    }*/

    def 'test json marshalling for outgoing relationships'(){

        when:


        rel = Relationship.link(de1, de2, rt)

        then:

        def dataElement = de1 as JSON
        def json = JSON.parse(dataElement.toString())

        expect:
        json
        json.id == de1.id
        json.name == de1.name
        json.outgoingRelationships == ["count":1, "link":"/dataElement/${de1.id}/outgoing"]

        when:

        Relationship.unlink(de1, de2, rt)

        then:

        de1.getRelations().size()==0
        de2.getRelations().size()==0



    }

}
