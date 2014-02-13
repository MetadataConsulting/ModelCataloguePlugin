package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */

class DataElementControllerISpec extends IntegrationSpec {

    @Shared
    def fixtureLoader, controller, rt, author, title, writer, rel

    def setupSpec() {

        RelationshipType.initDefaultRelationshipTypes()

        def springContext = WebApplicationContextUtils.getWebApplicationContext( SCH.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

        //load fixturess
        def fixtures =  fixtureLoader.load( "dataElements/DE_author","dataElements/DE_title","dataElements/DE_writer" )

        rt = RelationshipType.findByName("supersession")

        author = fixtures.DE_author
        writer = fixtures.DE_writer
        title = fixtures.DE_title


        controller = new DataElementController()

    }

/*
    def cleanupSpec(){
        author.delete()
        title.delete()
        writer.delete()
    }
*/
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
        json.list.any { it.id == author.id }
        json.list.any { it.id == writer.id }
        json.list.any { it.id == title.id }

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
        size    | id          | theParams
        3       | author.id   | [sort: "name", order: "asc"]
        2       | title.id    | [offset: 1, sort: "name", order: "desc"]
        2       | writer.id   | [max: 2, sort: "name", order: "desc"]
        1       | title.id    | [offset: 1, max: 1, sort: "name", order: "desc"]

    }

    void "Get an element that contains relationships"()
    {
        expect:
        DataElement.count()==3


        when:

        Relationship.link(author, writer, rt).save()

        controller.request.contentType = "text/json"
        controller.params.id = author.id
        controller.show()

        def result = controller.response.json

        then:
        result
        result.id == author.id
        result.name == author.name
        result.outgoingRelationships.destinationPath== ["/DataElement/$author.id"]
        result.outgoingRelationships.sourceName == ["$writer.name"]
        result.outgoingRelationships.sourcePath == ["/DataElement/$writer.id"]
        result.outgoingRelationships.destinationName == ["$author.name"]
        result.outgoingRelationships.relationshipType.sourceClass == ["uk.co.mc.core.PublishedElement"]
        result.outgoingRelationships.relationshipType.id == [rt.id]
        result.outgoingRelationships.relationshipType.sourceToDestination == ["superseded by"]
        result.outgoingRelationships.relationshipType.destinationClass == ["uk.co.mc.core.PublishedElement"]
        result.outgoingRelationships.relationshipType.name == ["supersession"]
        result.outgoingRelationships.relationshipType.getAt("class") == ["uk.co.mc.core.RelationshipType"]
        result.outgoingRelationships.relationshipType.destinationToSource == ["supersedes"]

        when:

        Relationship.unlink(author, writer, rt)

        then:

        true

    }

    void "If element not found "()
    {

        expect:
        DataElement.count()==3


        when:
        controller.params.id = 133
        controller.show()
        def result = controller.response

        then:
        result.status == 404

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
