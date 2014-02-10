package uk.co.mc.core

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */

class DataElementControllerISpec extends Specification {

    def controller
    def rt
    def rel
    def de1
    def de2
    def de3

    def setup() {

        def springContext = WebApplicationContextUtils.getWebApplicationContext( SCH.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        rt = new OntologyRelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        de1 = new DataElement(name: "One", description: "First data element", definition: "First data element definition").save()
        de2 = new DataElement(name: "Two", description: "Second data element", definition: "Second data element definition").save()


        rel = Relationship.link(de1, de2, rt)

        de3 = new DataElement(name: "Three",
                description: "Third data element",
                definition: "Third data element definition").save()

        controller = new DataElementController()

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

    void "Get list of data elements as JSON"() {

        expect:
        DataElement.count() == 3

        when:

        controller.index()

        def json = controller.response.json


        then:
        json.success
        json.size           == 3
        json.total          == 3
        json.list
        json.list.size()    == 3
        json.list.any { it.id == de1.id }
        json.list.any { it.id == de2.id }
        json.list.any { it.id == de3.id }

    }

    @Unroll
    void "Get list of data elements as JSON paged should have size #size and first element id #id for params #theParams"() {
        expect:
        DataElement.count() == 3

        when:
        theParams.each { key, val ->
            controller.params[key] = val
        }


        controller.index()
        def json = controller.response.json

        then:
        json.success
        json.size               == size
        json.total              == 3
        json.list
        json.list.first().id    == de2.id

        where:
        size    | theParams
        2       | [offset: 1, sort: "id", order: "desc"]
        2       | [max: 2, sort: "id", order: "desc"]
        1       | [offset: 1, max: 1, sort: "id", order: "desc"]

    }
    void "Get an element that contains relationships"()
    {
        expect:
         DataElement.count()==3

        when:

        controller.request.contentType = "text/json"
        controller.params.id = de1.id
        controller.show()

        def result = controller.response.json

        then:
        result.instance
        result.instance.id == de1.id
        result.instance.name == "One"
        result.instance.outgoingRelationships.destinationPath== ["/DataElement/$de2.id"]
        result.instance.outgoingRelationships.sourceName == ["Two"]
        result.instance.outgoingRelationships.sourcePath == ["/DataElement/$de1.id"]
        result.instance.outgoingRelationships.destinationName == ["One"]
        result.instance.outgoingRelationships.relationshipType.sourceClass == ["uk.co.mc.core.DataElement"]
        result.instance.outgoingRelationships.relationshipType.id == [5]
        result.instance.outgoingRelationships.relationshipType.sourceToDestination == ["SynonymousWith"]
        result.instance.outgoingRelationships.relationshipType.destinationClass == ["uk.co.mc.core.DataElement"]
        result.instance.outgoingRelationships.relationshipType.name == ["Synonym"]
        result.instance.outgoingRelationships.relationshipType.getAt("class") == ["uk.co.mc.core.OntologyRelationshipType"]
        result.instance.outgoingRelationships.relationshipType.destinationToSource == ["SynonymousWith"]

    }

    void "If element not found "()
    {

        expect:
        DataElement.count()==3


        when:
        controller.params.id = 133
        controller.show()
        def result = controller.response.json

        then:
        !result.instance
        result.errors

    }

//    void "Update an element"()
//    {
//        expect:
//        DataElement.count()==3
//
//        when:
//        controller.update()
//        def result = response.json
//
//        then:
//        result.instance
//        result.instance.id == 1
//        result.instance.name == "OneUpdated"
//
//    }

}
