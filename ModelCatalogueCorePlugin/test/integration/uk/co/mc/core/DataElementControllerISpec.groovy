package uk.co.mc.core


import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */

class DataElementControllerISpec extends Specification {

//    def controller
//
//    def setup() {
//
//        def springContext = WebApplicationContextUtils.getWebApplicationContext( SCH.servletContext )
//
//        //register custom json Marshallers
//        springContext.getBean('customObjectMarshallers').register()
//
//        def rt = new OntologyType(name:"Synonym",
//                sourceToDestination: "SynonymousWith",
//                destinationToSource: "SynonymousWith",
//                sourceClass: DataElement,
//                destinationClass: DataElement).save()
//
//        def de1 = new DataElement(id: 1, name: "One", description: "First data element", definition: "First data element definition").save()
//        def de2 = new DataElement(id: 2, name: "Two", description: "Second data element", definition: "Second data element definition").save()
//
//
//        def rel = new Relationship(source: de1,
//                destination: de2,
//                relationshipType: rt).save()
//
//        new DataElement(id:3, name: "Three",
//                description: "Third data element",
//                definition: "Third data element definition",
//                incomingRelationships: rel).save()
//
//        controller = new DataElementController()
//
//    }
//
//    def cleanup() {
//
//    }
//
//    void "Get list of data elements as JSON"() {
//
//        expect:
//        DataElement.count() == 3
//
//        when:
//
//        controller.index()
//
//        def json = controller.response.json
//
//        then:
//        json.success
//        json.size           == 3
//        json.total          == 3
//        json.list
//        json.list.size()    == 3
//        json.list.any { it.id == 1 }
//        json.list.any { it.id == 2 }
//        json.list.any { it.id == 3 }
//
//    }
//
//    @Unroll
//    void "Get list of data elements as JSON paged should have size #size and first element id #id for params #theParams"() {
//        expect:
//        DataElement.count() == 3
//
//        when:
//        theParams.each { key, val ->
//            controller.params[key] = val
//        }
//
//
//        controller.index()
//        def json = controller.response.json
//
//        then:
//        json.success
//        json.size               == size
//        json.total              == 3
//        json.list
//        json.list.first().id    == id
//
//
//        where:
//        size    | id  | theParams
//        2       | 5   | [offset: 1, sort: "id", order: "desc"]
//        2       | 9   | [max: 2, sort: "id", order: "desc"]
//        1       | 11  | [offset: 1, max: 1, sort: "id", order: "desc"]
//
//    }
//
//    void "Get an element that contains relationships"()
//    {
//        expect:
//         DataElement.count()==3
//
//
//        when:
//
//
//        controller.params.id = 15
//        controller.show()
//
//        def result = controller.response.json
//
//        then:
//        result.instance
//        result.instance.id == 15
//        result.instance.name == "Three"
//        result.instance.incomingRelationships == [
//                [
//                        destinationPath:"/DataElement/14",
//                        relationshipType:[
//                                sourceClass:"uk.co.mc.core.DataElement",
//                                id:5,
//                                sourceToDestination:"SynonymousWith",
//                                destinationClass:"uk.co.mc.core.DataElement",
//                                name:"Synonym",
//                                class:"uk.co.mc.core.RelationshipType",
//                                destinationToSource:"SynonymousWith"],
//                        sourceName:"Three",
//                        sourcePath:"/DataElement/15",
//                        destinationName:"Two"]]
//    }
//
//    void "If element not found "()
//    {
//
//        expect:
//        DataElement.count()==3
//
//
//        when:
//        controller.params.id = 133
//        controller.show()
//        def result = controller.response.json
//
//        then:
//        !result.instance
//        result.errors
//
//    }

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
