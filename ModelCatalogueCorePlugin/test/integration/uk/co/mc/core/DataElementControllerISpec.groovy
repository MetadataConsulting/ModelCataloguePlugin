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

    @Shared
    def controller, rt, de1, de2,de3, rel

    def setupSpec() {

        def springContext = WebApplicationContextUtils.getWebApplicationContext( SCH.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        rt = new RelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        de1 = new DataElement(id: 1, name: "One", description: "First data element").save()
        de2 = new DataElement(id: 2, name: "Two", description: "Second data element").save()


        rel = Relationship.link(de1, de2, rt).save()

        de3 = new DataElement(id:3, name: "Three",
                description: "Third data element").save()

        controller = new DataElementController()

    }

    /*def cleanupSpec(){

        Relationship.unlink(de1, de2, rt)
        de1.delete()
        de2.delete()
        de3.delete()
        rt.delete()

    }*/

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
        json.list.first().id    == id


        where:
        size    | id       | theParams
        2       | de2.id   | [offset: 1, sort: "id", order: "desc"]
        2       | de3.id   | [max: 2, sort: "id", order: "desc"]
        1       | de2.id   | [offset: 1, max: 1, sort: "id", order: "desc"]

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
        result.instance.outgoingRelationships.destinationPath== ["/DataElement/$de1.id"]
        result.instance.outgoingRelationships.sourceName == ["$de2.name"]
        result.instance.outgoingRelationships.sourcePath == ["/DataElement/$de2.id"]
        result.instance.outgoingRelationships.destinationName == ["$de1.name"]
        result.instance.outgoingRelationships.relationshipType.sourceClass == ["uk.co.mc.core.DataElement"]
        result.instance.outgoingRelationships.relationshipType.id == [rt.id]
        result.instance.outgoingRelationships.relationshipType.sourceToDestination == ["SynonymousWith"]
        result.instance.outgoingRelationships.relationshipType.destinationClass == ["uk.co.mc.core.DataElement"]
        result.instance.outgoingRelationships.relationshipType.name == ["Synonym"]
        result.instance.outgoingRelationships.relationshipType.getAt("class") == ["uk.co.mc.core.RelationshipType"]
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
