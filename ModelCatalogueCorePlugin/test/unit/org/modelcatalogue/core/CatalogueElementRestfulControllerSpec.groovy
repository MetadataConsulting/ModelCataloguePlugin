package org.modelcatalogue.core

import grails.converters.JSON
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.util.marshalling.ElementsMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipMarshallers
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Abstract parent for restful controllers specification.
 *
 * The concrete subclass must use {@link grails.test.mixin.web.ControllerUnitTestMixin}.
 * The concrete subclass must use @Mixin(ResultRecorder)
 *
 */

abstract class CatalogueElementRestfulControllerSpec<T> extends AbstractRestfulControllerSpec {

    def setup() {
        setupMimeTypes()
        [marshallers, [new RelationshipMarshallers(), new RelationshipsMarshaller(), new ElementsMarshaller()]].flatten().each {
            it.register()
        }
    }

    @Unroll
    def "Link existing elements using add to #direction endpoint with JSON result"(){
        def type = prepareTypeAndDummyEntities()

        response.format = 'json'
        request.json = loadItem2 as JSON
        controller."add${direction.capitalize()}"(loadItem1.id, type.name)
        def json = response.json

        recordResult "add${direction.capitalize()}", json

        def expectedSource =        direction == "outgoing" ? loadItem1 : loadItem2
        def expectedDestination =   direction == "outgoing" ? loadItem2 : loadItem1

        expect:
        response.status == HttpServletResponse.SC_CREATED
        json.source
        json.source.id      == expectedSource.id
        json.destination
        json.destination.id == expectedDestination.id
        json.type
        json.type.id        == type.id

        where:
        direction << ["incoming", "outgoing"]
    }

    @Unroll
    def "Link existing elements using add to #direction endpoint with XML result"(){
        def type = prepareTypeAndDummyEntities()

        response.format = 'xml'
        request.xml = loadItem2.encodeAsXML()
        controller."add${direction.capitalize()}"(loadItem1.id, type.name)
        def xml = response.xml

        recordResult "add${direction.capitalize()}", xml

        def expectedSource =        direction == "outgoing" ? loadItem1 : loadItem2
        def expectedDestination =   direction == "outgoing" ? loadItem2 : loadItem1

        expect:
        response.status             == HttpServletResponse.SC_CREATED
        xml.source
        xml.source.@id.text()       == "${expectedSource.id}"
        xml.destination
        xml.destination.@id.text()  == "${expectedDestination.id}"
        xml.type
        xml.type.@id.text()         == "${type.id}"

        where:
        direction << ["incoming", "outgoing"]
    }

    @Unroll
    def "Unlink non existing elements using add to #direction endpoint with #format result"(){
        def type = prepareTypeAndDummyEntities()

        if (direction == "outgoing") {
            Relationship.unlink(loadItem1, loadItem2, type)
        } else {
            Relationship.unlink(loadItem2, loadItem1, type)
        }

        response.format = format.toLowerCase()

        def input = loadItem2."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"

        if(format=="JSON"){
            recordInputJSON fixtureName, input
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }


        request."${format.toLowerCase()}"= input

        controller."remove${direction.capitalize()}"(loadItem1.id, type.name)


        expect:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format | direction
        "XML"  | "outgoing"
        "JSON" | "outgoing"
        "XML"  | "incoming"
        "JSON" | "incoming"
    }

    @Unroll
    def "Unlink existing elements using add to #direction endpoint with #format result"(){
        Relationship.metaClass.static.findBySourceAndDestinationAndRelationshipType = { CatalogueElement source, CatalogueElement destination, RelationshipType type ->
            source.outgoingRelationships.find { it.relationshipType == type && it.destination == destination}
        }


        def type = prepareTypeAndDummyEntities()

        if (direction == "outgoing") {
            Relationship.link(loadItem1, loadItem2, type)
        } else {
            Relationship.link(loadItem2, loadItem1, type)
        }

        response.format = format.toLowerCase()

        def input = loadItem2."encodeAs$format"()

        String fixtureName = "removeNonExisting${direction.capitalize()}$format"

        if(format=="JSON"){
            recordInputJSON fixtureName, input
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }

        request."${format.toLowerCase()}"= input

        controller."remove${direction.capitalize()}"(loadItem1.id, type.name)


        expect:
        response.status == HttpServletResponse.SC_NO_CONTENT

        where:
        format | direction
        "XML"  | "outgoing"
        "JSON" | "outgoing"
        "XML"  | "incoming"
        "JSON" | "incoming"
    }

    @Unroll
    def "Link existing elements using add to #direction endpoint with failing constraint as JSON result"(){
        def type = prepareTypeAndDummyEntities()
        type.rule = "return false"
        type.save()

        response.format = 'json'
        request.json = loadItem2 as JSON
        controller."add${direction.capitalize()}"(loadItem1.id, type.name)
        def json = response.json

        recordResult "add${direction.capitalize()}Failed", json

        expect:
        response.status == 422 // unprocessable entity
        json.errors
        json.errors.size() >= 1
        json.errors.first().field == 'relationshipType'

        where:
        direction << ["incoming", "outgoing"]
    }


    @Unroll
    def "Link existing elements using add to #direction endpoint with failing constraint as XML result"(){
        def type = prepareTypeAndDummyEntities()
        type.rule = "return false"
        type.save()

        response.format = 'xml'
        request.xml = loadItem2.encodeAsXML()
        controller."add${direction.capitalize()}"(loadItem1.id, type.name)
        def xml = response.xml

        recordResult "add${direction.capitalize()}Failed", xml

        expect:
        response.status == 422 // unprocessable entity
        xml.name() == "errors"
        xml.error.size() >= 1
        xml.error[0].@field.text() == 'relationshipType'

        where:
        direction << ["incoming", "outgoing"]
    }


    @Unroll
    def "#action with non-existing one to #direction endpoint with #format result"(){
        def type = prepareTypeAndDummyEntities()

        response.format = format.toLowerCase()

        def item = resource.newInstance()
        item.id = 1000000

        def input = item."encodeAs$format"()

        String fixtureName = "removeNonExisting${direction.capitalize()}$format"

        if(format=="JSON"){
            recordInputJSON fixtureName, input
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }

        request."${format.toLowerCase()}"= input
        controller."${action}${direction.capitalize()}"(loadItem1.id, type.name)


        expect:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        action   | format | direction
        "add"    | "XML"  | "outgoing"
        "add"    | "JSON" | "outgoing"
        "add"    | "XML"  | "incoming"
        "add"    | "JSON" | "incoming"
        "remove" | "XML"  | "outgoing"
        "remove" | "JSON" | "outgoing"
        "remove" | "XML"  | "incoming"
        "remove" | "JSON" | "incoming"
    }

    @Unroll
    def "#action with not-existing type to #direction endpoint with #format result"(){
        response.format = format.toLowerCase()

        def input = loadItem2."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"

        if(format=="JSON"){
            recordInputJSON fixtureName, input
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }

        request."${format.toLowerCase()}" = input
        controller."${action}${direction.capitalize()}"(loadItem1.id, "no-such-type")

        expect:
        response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        action   | format | direction
        "add"    | "XML"  | "outgoing"
        "add"    | "JSON" | "outgoing"
        "add"    | "XML"  | "incoming"
        "add"    | "JSON" | "incoming"
        "remove" | "XML"  | "outgoing"
        "remove" | "JSON" | "outgoing"
        "remove" | "XML"  | "incoming"
        "remove" | "JSON" | "incoming"

    }

    protected RelationshipType prepareTypeAndDummyEntities() {
        fixturesLoader.load('relationshipTypes/RT_relationship')
        fillWithDummyEntities(15)
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        assert relationshipType
        relationshipType
    }

    protected linkRelationshipsToDummyEntities(String incomingOrOutgoing) {
        mockDynamicFindersForRelationships()
        RelationshipType relationshipType = prepareTypeAndDummyEntities()

        def first = resource.get(1)
        first."${incomingOrOutgoing}Relationships" = first."${incomingOrOutgoing}Relationships" ?: []

        for (unit in resource.list()) {
            if (unit != first) {
                if (incomingOrOutgoing == "incoming") {
                    assert !Relationship.link(unit, first, relationshipType).hasErrors()
                } else {
                    assert !Relationship.link(first, unit, relationshipType).hasErrors()
                }
                if (first."${incomingOrOutgoing}Relationships".size() == 12) {
                    break
                }
            }
        }

        assert first."${incomingOrOutgoing}Relationships"
        assert first."${incomingOrOutgoing}Relationships".size() == 12
        first
    }

    private static void mockDynamicFindersForRelationships() {
        Relationship.metaClass.static.findAllBySource = { CatalogueElement el, params = [:] ->
            if (!el.outgoingRelationships) {
                return []
            }
            el.outgoingRelationships.drop(params.offset as Integer ?: 0).take(params.max as Integer ?: 0)
        }

        Relationship.metaClass.static.findAllByDestination = { CatalogueElement el, params = [:] ->
            if (!el.incomingRelationships) {
                return []
            }
            el.incomingRelationships.drop(params.offset as Integer ?: 0).take(params.max as Integer ?: 0)
        }
        Relationship.metaClass.static.findAllBySourceAndRelationshipType = { CatalogueElement el, RelationshipType type, params = [:] ->
            if (!el.outgoingRelationships) {
                return []
            }
            el.outgoingRelationships.findAll {
                it.relationshipType == type
            }.drop(params.offset as Integer ?: 0).take(params.max as Integer ?: 0)
        }

        Relationship.metaClass.static.findAllByDestinationAndRelationshipType = { CatalogueElement el, RelationshipType type, params = [:] ->
            if (!el.incomingRelationships) {
                return []
            }
            el.incomingRelationships.findAll {
                it.relationshipType == type
            }.drop(params.offset as Integer ?: 0).take(params.max as Integer ?: 0)
        }
        Relationship.metaClass.static.countBySourceAndRelationshipType = { CatalogueElement el, RelationshipType type ->
            if (!el.outgoingRelationships) {
                return 0
            }
            el.outgoingRelationships.count { it.relationshipType == type }
        }

        Relationship.metaClass.static.countByDestinationAndRelationshipType = { CatalogueElement el, RelationshipType type ->
            if (!el.incomingRelationships) {
                return 0
            }
            el.incomingRelationships.count { it.relationshipType == type }
        }
    }

    private checkJsonRelationsInternal(typeParam, no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)

        response.format = "json"
        params.offset = offset
        params.id = first.id

        controller."${incomingOrOutgoing}"(max, typeParam)
        JSONElement json = response.json


        recordResult "${incomingOrOutgoing}${no}", json


        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)

        def item = json.list[0]


        assert item.type
        assert item.type.name == "relationship"
        assert item.type.sourceToDestination == "relates to"
        assert item.direction == incomingOrOutgoing == "incoming" ? "destinationToSource" : "sourceToDestination"
        assert item.type.destinationToSource == "is relationship of"
        assert item.relation
        assert item.relation.id
        assert item.relation.elementType
        assert item.removeLink == "/${resourceName}/${first.id}/${incomingOrOutgoing}/relationship"


        def relation = Class.forName(item.relation.elementType).get(item.relation.id)

        assert item.relation.name == relation.name
        assert item.relation.id == relation.id
    }

    protected static checkJsonCorrectListValues(JSONElement json, total, size, offset, max, next, previous) {
        assert json.success
        assert json.total == total
        assert json.size == size
        assert json.offset == offset
        assert json.page == max
        assert json.list
        assert json.list.size() == size
        assert json.next == next
        assert json.previous == previous
        true
    }

    def checkJsonRelations(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal(null, no, size, max, offset, total, next, previous, incomingOrOutgoing)
    }

    def checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal("relationship", no, size, max, offset, total, next, previous, incomingOrOutgoing)
    }

    def checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)

        response.format = "json"
        params.offset = offset
        params.id = first.id

        RelationshipType type2 = new RelationshipType(name: "xyz", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "xyz", destinationToSource: "zyx")
        assert type2.save()

        controller."${incomingOrOutgoing}"(max, type2.name)
        JSONObject json = response.json


        recordResult "${incomingOrOutgoing}WithNonExistingType${no}", json


        assert json.success
        assert !json.list
        assert json.total == 0
        assert json.size == 0
        assert json.offset == offset
        assert json.page == max

        type2.delete()
    }

    def checkXmlRelationsInternal(typeParam, no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)

        response.format = "xml"
        params.offset = offset
        params.id = first.id

        controller."${incomingOrOutgoing}"(max, typeParam)
        GPathResult result = response.xml


        recordResult "${incomingOrOutgoing}${no}", result


        assert result
        checkXmlCorrectListValues(result, total, size, offset, max, next, previous)
        assert result.relationship
        assert result.relationship.size() == size

        def item = result.relationship[0]


        assert item.type
        assert item.type.name == "relationship"
        assert item.type.sourceToDestination == "relates to"
        assert item.direction == incomingOrOutgoing == "incoming" ? "destinationToSource" : "sourceToDestination"
        assert item.type.destinationToSource == "is relationship of"
        assert item.relation
        assert item.relation.@id
        assert item.relation.@elementType

        assert item.@removeLink.text() ==  "/${resourceName}/${first.id}/${incomingOrOutgoing}/relationship"


        def relation = Class.forName(item.relation.@elementType.text()).get(item.relation.@id.text() as Long)

        assert item.relation.name == relation.name
        assert item.relation.@id == "${relation.id}"
    }



    def checkXmlRelations(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkXmlRelationsInternal(null, no, size, max, offset, total, next, previous, incomingOrOutgoing)
    }

    def checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkXmlRelationsInternal("relationship", no, size, max, offset, total, next, previous, incomingOrOutgoing)
    }

    def checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)

        response.format = "xml"
        params.offset = offset
        params.id = first.id

        RelationshipType type2 = new RelationshipType(name: "xyz", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "xyz", destinationToSource: "zyx")
        assert type2.save()

        controller."${incomingOrOutgoing}"(max, type2.name)
        GPathResult result = response.xml


        recordResult "${incomingOrOutgoing}WithNonExistingType${no}", result


        assert result
        assert result.@success.text() == "true"
        assert result.@total.text() == "0"
        assert result.@offset.text() == "${offset}"
        assert result.@page.text() == "${max}"
        assert result.@size.text() == "0"
        assert result.relationship.isEmpty()

        type2.delete()
    }

    def "Return 404 for non-existing item as JSON for incoming relationships queried by type"() {
        response.format = "json"

        params.id = "1"

        controller.incoming(10, "no-such-type")

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML for incoming relationships queried by type"() {
        response.format = "xml"

        params.id = "1"
        controller.incoming(10, "no-such-type")

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as JSON for outgoing relationships queried by type"() {
        response.format = "json"

        params.id = "1"

        controller.outgoing(10, "no-such-type")

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML for outgoing relationships queried by type"() {
        response.format = "xml"

        params.id = "1"

        controller.outgoing(10, "no-such-type")

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }


    def "Return 404 for non-existing item as JSON for incoming relationships"() {
        response.format = "json"

        params.id = "1000000"

        controller.incoming(10, null)

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML for incoming relationships"() {
        response.format = "xml"

        params.id = "1000000"

        controller.incoming(10, null)

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as JSON for outgoing relationships"() {
        response.format = "json"

        params.id = "1000000"

        controller.outgoing(10, null)

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML for outgoing relationships"() {
        response.format = "xml"

        params.id = "1000000"

        controller.outgoing(10, null)

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }



    def xmlCustomPropertyCheck(xml, item){

        Object.xmlCustomPropertyCheck(xml, item)

        checkProperty(xml.name, item.name, "name")
        checkProperty(xml.description, item.description, "description")
        checkProperty(xml.@elementType, item.class.name, "elementType")
        checkProperty(xml.@elementTypeName, GrailsNameUtils.getNaturalName(item.class.simpleName), "elementTypeName")
        checkProperty(xml.incomingRelationships.@count, (item?.incomingRelationships)?item.incomingRelationships.size(): 0, "incomingRelationships")
        checkProperty(xml.incomingRelationships.@link, "/${resourceName}/${item.id}/incoming", "incomingRelationships")
        checkProperty(xml.outgoingRelationships.@count, (item?.outgoingRelationships)?item.outgoingRelationships.size(): 0, "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@link, "/${resourceName}/${item.id}/outgoing", "outgoingRelationships")

        return true
    }

    def xmlCustomPropertyCheck(inputItem, xml, outputItem){

        Object.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.name, inputItem.name, "name")
        checkProperty(xml.description, inputItem.description, "description")
        checkProperty(xml.@elementType, inputItem.class.name, "elementType")
        checkProperty(xml.@elementTypeName, GrailsNameUtils.getNaturalName(inputItem.class.simpleName), "elementTypeName")
        checkProperty(xml.incomingRelationships.@count, (outputItem?.incomingRelationships)?outputItem.incomingRelationships.size(): 0, "incomingRelationships")
        checkProperty(xml.incomingRelationships.@link, "/${resourceName}/${outputItem.id}/incoming", "incomingRelationships")
        checkProperty(xml.outgoingRelationships.@count, (outputItem?.outgoingRelationships)?outputItem.outgoingRelationships.size(): 0, "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@link, "/${resourceName}/${outputItem.id}/outgoing", "outgoingRelationships")

        return true
    }


    def customJsonPropertyCheck(item, json){

        Object.customJsonPropertyCheck(item, json)
        checkProperty(json.name , item.name, "name")
        checkProperty(json.description , item.description, "description")
        checkProperty(json.elementType , item.class.name, "elementType")
        checkProperty(json.elementTypeName , GrailsNameUtils.getNaturalName(item.class.simpleName), "elementTypeName")
        checkProperty(json.outgoingRelationships.count, (item?.outgoingRelationships)?item.outgoingRelationships.size(): 0, "outgoingCount")
        checkProperty(json.outgoingRelationships.link, "/${resourceName}/${item.id}/outgoing", "outgoingLink")
        checkProperty(json.incomingRelationships.count, (item?.incomingRelationships)?item.incomingRelationships.size(): 0, "incomingCount")
        checkProperty(json.incomingRelationships.link, "/${resourceName}/${item.id}/incoming", "incomingLink")
        return true
    }




    def customJsonPropertyCheck(inputItem, json, outputItem){

        Object.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.name , inputItem.name, "name")
        checkProperty(json.description , inputItem.description, "description")
        checkProperty(json.elementType , inputItem.class.name, "elementType")
        checkProperty(json.elementTypeName , GrailsNameUtils.getNaturalName(inputItem.class.simpleName), "elementTypeName")
        checkProperty(json.outgoingRelationships.count, (outputItem?.outgoingRelationships)?outputItem.outgoingRelationships.size(): 0, "outgoingCount")
        checkProperty(json.outgoingRelationships.link, "/${resourceName}/${outputItem.id}/outgoing", "outgoingLink")
        checkProperty(json.incomingRelationships.count, (outputItem?.incomingRelationships)?outputItem.incomingRelationships.size(): 0, "incomingCount")
        checkProperty(json.incomingRelationships.link, "/${resourceName}/${outputItem.id}/incoming", "incomingLink")

        return true

    }




    // Following needs to be copied to subclasses. Grails mocking framework is not yet capable of handling such
    // level of abstraction

    /*
     // -- begin copy and pasted

    @Unroll
    def "get json outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
    }

    @Unroll
    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
    }


    @Unroll
    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
    }

    @Unroll
    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
    }


    @Unroll
    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
    }

    @Unroll
    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
    }

    @Unroll
    def "get xml outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
    }

    @Unroll
    def "get xml incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
    }


    @Unroll
    def "get xml outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
    }

    @Unroll
    def "get xml incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
    }


    @Unroll
    def "get xml outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
    }

    @Unroll
    def "get xml incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        cleanup:
        RelationshipType.findByName("relationship")?.delete()

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
    }

    // -- end copy and pasted
     */

}
