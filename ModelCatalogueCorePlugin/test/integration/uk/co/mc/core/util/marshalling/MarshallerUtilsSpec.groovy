package uk.co.mc.core.util.marshalling

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
class MarshallerUtilsSpec extends IntegrationSpec{

    @Shared
    def fixtureLoader, de1, de2, de3, rel, rel2, rt


    def setupSpec(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        def fixtures =  fixtureLoader.load( "dataElements/DE_author6", "dataElements/DE_author7", "dataElements/DE_author8", "relationshipTypes/RT_relatedTerm")

        de1 = fixtures.DE_author6
        de2 = fixtures.DE_author7
        de3 = fixtures.DE_author8
        rt = fixtures.RT_relatedTerm

    }
/*
    def cleanupSpec(){
       de1.delete()
       de2.delete()
       de3.delete()
   }*/


    def "test json marshalling for outgoing relationships"(){


        def dataElement1 = DataElement.get(de1.id)
        def dataElement2 = DataElement.get(de2.id)
        def dataElement3 = DataElement.get(de3.id)

        when:

        Relationship.link(dataElement1, dataElement2, rt)
        Relationship.link(dataElement1, dataElement3, rt)

        then:

        def marshalledOutput = MarshallerUtils.marshallOutgoingRelationships(dataElement1)

        marshalledOutput[0].sourcePath =="/DataElement/$dataElement2.id"
        marshalledOutput[0].destinationPath =="/DataElement/$dataElement1.id"
        marshalledOutput[0].destinationName =="$dataElement1.name"
        marshalledOutput[0].relationshipType.name =="RelatedTerm"
        marshalledOutput[0].sourceName =="$dataElement2.name"
        marshalledOutput[1].sourcePath =="/DataElement/$dataElement3.id"
        marshalledOutput[1].destinationPath =="/DataElement/$dataElement1.id"
        marshalledOutput[1].destinationName =="$dataElement1.name"
        marshalledOutput[1].relationshipType.name =="RelatedTerm"
        marshalledOutput[1].sourceName =="$dataElement3.name"

        when:

        Relationship.unlink(dataElement1, dataElement2, rt)
        Relationship.unlink(dataElement1, dataElement3, rt)

        then:

        dataElement1.getRelations().size()==0
        dataElement2.getRelations().size()==0
        dataElement3.getRelations().size()==0

    }



}
