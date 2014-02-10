package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.OntologyRelationshipType
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType

/**
 * Created by adammilward on 10/02/2014.
 */
class DataElementMarshallerSpec extends Specification{

    def setup(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

    }

    def cleanup(){
        Relationship.list().each{ relationship ->
            Relationship.unlink(relationship.source, relationship.destination, relationship.relationshipType)
        }

        DataElement.list().each{ dataElement ->
            dataElement.delete()
        }

        RelationshipType.list().each{ relationshipType ->
            relationshipType.delete()
        }
    }

    def "test json marshalling for incoming relationships"(){

        expect:

        DataElement.list().isEmpty()

        when:

        def de1 = new DataElement(id: 1, name: "One", description: "First data element", definition: "First data element definition").save()
        def de2 = new DataElement(id: 2, name: "Two", description: "Second data element", definition: "Second data element definition").save()
        def de3 = new DataElement(id: 3, name: "Three", description: "Third data element", definition: "Third data element definition").save()

        def rt = new OntologyRelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        def rel = Relationship.link(de1, de2, rt)
        def rel2 = Relationship.link(de1, de3, rt)

        then:

        def de1JSON = de1 as JSON
        def de2JSON = de2 as JSON
        def de3JSON = de3 as JSON

        de1JSON.toString() == '{"id":1,"name":"One","description":"First data element","status":{"enumType":"uk.co.mc.core.PublishedElement$Status","name":"DRAFT"},"versionNumber":0.1,"incomingRelationships":[],"outgoingRelationships":[{"sourcePath":"/DataElement/3","sourceName":"Three","destinationPath":"/DataElement/1","destinationName":"One","relationshipType":{"class":"uk.co.mc.core.OntologyRelationshipType","id":1,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"SynonymousWith","name":"Synonym","sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"SynonymousWith"}},{"sourcePath":"/DataElement/2","sourceName":"Two","destinationPath":"/DataElement/1","destinationName":"One","relationshipType":{"class":"uk.co.mc.core.OntologyRelationshipType","id":1,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"SynonymousWith","name":"Synonym","sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"SynonymousWith"}}]}'

        de2JSON.toString() == '{"id":2,"name":"Two","description":"Second data element","status":{"enumType":"uk.co.mc.core.PublishedElement$Status","name":"DRAFT"},"versionNumber":0.1,"incomingRelationships":[{"destinationPath":"/DataElement/2","destinationName":"Two","sourcePath":"/DataElement/2","sourceName":"Two","relationshipType":{"class":"uk.co.mc.core.OntologyRelationshipType","id":1,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"SynonymousWith","name":"Synonym","sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"SynonymousWith"}}],"outgoingRelationships":[]}'

        de3JSON.toString() == '{"id":3,"name":"Three","description":"Third data element","status":{"enumType":"uk.co.mc.core.PublishedElement$Status","name":"DRAFT"},"versionNumber":0.1,"incomingRelationships":[{"destinationPath":"/DataElement/3","destinationName":"Three","sourcePath":"/DataElement/3","sourceName":"Three","relationshipType":{"class":"uk.co.mc.core.OntologyRelationshipType","id":1,"destinationClass":"uk.co.mc.core.DataElement","destinationToSource":"SynonymousWith","name":"Synonym","sourceClass":"uk.co.mc.core.DataElement","sourceToDestination":"SynonymousWith"}}],"outgoingRelationships":[]}'



    }

}
