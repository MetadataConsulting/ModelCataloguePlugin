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
        json.outgoingRelationships.destinationPath== ["/DataElement/$de1.id"]
        json.outgoingRelationships.sourceName == ["$de2.name"]
        json.outgoingRelationships.sourcePath == ["/DataElement/$de2.id"]
        json.outgoingRelationships.destinationName == ["$de1.name"]
        json.outgoingRelationships.relationshipType.sourceClass == ["uk.co.mc.core.DataElement"]
        json.outgoingRelationships.relationshipType.id == [rt.id]
        json.outgoingRelationships.relationshipType.sourceToDestination == ["AntonymousWith"]
        json.outgoingRelationships.relationshipType.destinationClass == ["uk.co.mc.core.DataElement"]
        json.outgoingRelationships.relationshipType.name == ["Antonym"]
        json.outgoingRelationships.relationshipType.getAt("class") == ["uk.co.mc.core.RelationshipType"]
        json.outgoingRelationships.relationshipType.destinationToSource == ["AntonymousWith"]



        when:

        Relationship.unlink(de1, de2, rt)

        then:

        de1.getRelations().size()==0
        de2.getRelations().size()==0



    }

}
