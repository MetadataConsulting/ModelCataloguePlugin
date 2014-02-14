package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared
import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType

/**
 * Created by adammilward on 10/02/2014.
 */
class DataElementMarshallerSpec extends IntegrationSpec{

    @Shared
    def fixtureLoader, de1, de2, de3, rel, rel2, rt


    def setupSpec(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        def fixtures =  fixtureLoader.load( "dataElements/DE_author9", "dataElements/DE_author10", "dataElements/DE_author11", "relationshipTypes/RT_antonym")

        de1 = fixtures.DE_author9
        de2 = fixtures.DE_author10
        de3 = fixtures.DE_author11
        rt = fixtures.RT_antonym

    }

    /*
    def cleanupSpec(){
        de1.delete()
        de2.delete()
        de3.delete()
        rt.delete()
    }*/

    def 'test json marshalling for incoming relationships'(){

        when:


        rel = Relationship.link(de1, de2, rt)
        rel2 = Relationship.link(de1, de3, rt)

        then:

        def de1JSON = de1 as JSON

        de1JSON.toString() == '{"id":28,"name":"auth9","description":"the DE_author of the book","status":{"enumType":"uk.co.mc.core.PublishedElement$Status","name":"DRAFT"},"versionNumber":0.1,"incomingRelationships":[],"outgoingRelationships":[{"sourcePath":"/DataElement/30","sourceName":"auth10","destinationPath":"/DataElement/28","destinationName":"auth9","relationshipType":{"class":"uk.co.mc.core.RelationshipType","id":14,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"AntonymousWith","name":"Antonym","rule":null,"sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"AntonymousWith"}},{"sourcePath":"/DataElement/29","sourceName":"auth11","destinationPath":"/DataElement/28","destinationName":"auth9","relationshipType":{"class":"uk.co.mc.core.RelationshipType","id":14,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"AntonymousWith","name":"Antonym","rule":null,"sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"AntonymousWith"}}]}'

        when:

        Relationship.unlink(de1, de2, rt)
        Relationship.unlink(de1, de3, rt)

        then:

        de1.getRelations().size()==0
        de2.getRelations().size()==0



    }

}
