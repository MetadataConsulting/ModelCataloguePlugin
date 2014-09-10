package org.modelcatalogue.core

import grails.converters.JSON
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.util.Mappings
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 27/02/2014.
 */
abstract class AbstractCatalogueElementControllerIntegrationSpec<T> extends AbstractControllerIntegrationSpec implements ResultRecorder{


    @Unroll
    def "Link existing elements using add to #direction endpoint with JSON result"(){

        controller.response.format = 'json'
        controller.request.method       = 'POST'
        controller.request.json = loadItem as JSON
        controller."add${direction.capitalize()}"(anotherLoadItem.id, relationshipType.name)
        def json = controller.response.json
        recordResult "add${direction.capitalize()}", json
        def expectedSource =        direction == "outgoing" ? anotherLoadItem : loadItem
        def expectedDestination =   direction == "outgoing" ? loadItem : anotherLoadItem

        expect:
        controller.response.status == HttpServletResponse.SC_CREATED
        json.source
        json.source.id      == expectedSource.id
        json.destination
        json.destination.id == expectedDestination.id
        json.type
        json.type.id        == relationshipType.id
        resource.count() == totalCount

        where:
        direction << ["incoming", "outgoing"]
    }

    @Unroll
    def "Link existing elements using add to #direction endpoint with XML result"(){

        controller.request.method       = 'POST'
        controller.response.format = 'xml'
        controller.request.xml = anotherLoadItem.encodeAsXML()
        controller."add${direction.capitalize()}"(loadItem.id, relationshipType.name)
        def xml = controller.response.xml
        recordResult "add${direction.capitalize()}", xml
        def expectedSource =        direction == "outgoing" ? loadItem : anotherLoadItem
        def expectedDestination =   direction == "outgoing" ? anotherLoadItem : loadItem

        expect:
        controller.response.status             == HttpServletResponse.SC_CREATED
        xml.source
        xml.source.@id.text()       == "${expectedSource.id}"
        xml.destination
        xml.destination.@id.text()  == "${expectedDestination.id}"
        xml.type
        xml.type.@id.text()         == "${relationshipType.id}"
        resource.count() == totalCount

        where:
        direction << ["incoming", "outgoing"]
    }


    @Unroll
    def "Unlink non existing elements using add to #direction endpoint with #format result"(){

        controller.request.method       = 'DELETE'
        if (direction == "outgoing") {
            controller.relationshipService.unlink(loadItem, anotherLoadItem, relationshipType)
        } else {
            controller.relationshipService.unlink(anotherLoadItem, loadItem, relationshipType)
        }
        controller.response.format = format.toLowerCase()
        def input = anotherLoadItem."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"
        if(format=="JSON"){
            recordInputJSON fixtureName, input.toString()
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }
        controller.request."${format.toLowerCase()}"= input
        controller."remove${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        format | direction
        "XML"  | "outgoing"
        "JSON" | "outgoing"
        "XML"  | "incoming"
        "JSON" | "incoming"
    }

    @Unroll
    def "Unlink existing elements using add to #direction endpoint with #format result"(){
        controller.request.method       = 'DELETE'
        if (direction == "outgoing") {
            controller.relationshipService.link(loadItem, anotherLoadItem, relationshipType)
        } else {
            controller.relationshipService.link(anotherLoadItem, loadItem, relationshipType)
        }
        controller.response.format = format.toLowerCase()
        def input = anotherLoadItem."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"
        if(format=="JSON"){
            recordInputJSON fixtureName, input.toString()
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }
        controller.request."${format.toLowerCase()}"= input
        controller."remove${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NO_CONTENT
        resource.count() == totalCount

        where:
        format | direction
        "XML"  | "outgoing"
        "JSON" | "outgoing"
        "XML"  | "incoming"
        "JSON" | "incoming"
    }


    @Unroll
    def "Link existing elements using add to #direction endpoint with failing constraint as JSON result"(){
        controller.request.method       = 'POST'
        RelationshipType relationshipType = RelationshipType.findByName("falseRuleReturn")
        controller.response.format = 'json'
        controller.request.json = anotherLoadItem as JSON
        controller."add${direction.capitalize()}"(loadItem.id, relationshipType.name)
        def json = controller.response.json
        recordResult "add${direction.capitalize()}Failed", json

        expect:
        controller.response.status == 422 // unprocessable entity
        json.errors
        json.errors.size() >= 1
        json.errors.first().field == 'relationshipType'
        resource.count() == totalCount

        where:
        direction << ["incoming", "outgoing"]
    }


    @Unroll
    def "Link existing elements using add to #direction endpoint with failing constraint as XML result"(){
        controller.request.method       = 'POST'
        RelationshipType relationshipType = RelationshipType.findByName("falseRuleReturn")
        controller.response.format = 'xml'
        controller.request.xml = anotherLoadItem.encodeAsXML()
        controller."add${direction.capitalize()}"(loadItem.id, relationshipType.name)
        def xml = controller.response.xml
        recordResult "add${direction.capitalize()}Failed", xml

        expect:
        controller.response.status == 422 // unprocessable entity
        xml.name() == "errors"
        xml.error.size() >= 1
        xml.error[0].@field.text() == 'relationshipType'
        resource.count() == totalCount

        where:
        direction << ["incoming", "outgoing"]
    }

    @Unroll
    def "#action with non-existing one to #direction endpoint with #format result"(){
        controller.request.method       = method
        controller.response.format = format.toLowerCase()
        def item = newResourceInstance()
        item.id = 1000000
        def input = item."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"
        if(format=="JSON"){
            recordInputJSON fixtureName, input.toString()
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }
        controller.request."${format.toLowerCase()}"= input
        controller."${action}${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        action   | format | direction  | method
        "add"    | "XML"  | "outgoing" | "POST"
        "add"    | "JSON" | "outgoing" | "POST"
        "add"    | "XML"  | "incoming" | "POST"
        "add"    | "JSON" | "incoming" | "POST"
        "remove" | "XML"  | "outgoing" | "DELETE"
        "remove" | "JSON" | "outgoing" | "DELETE"
        "remove" | "XML"  | "incoming" | "DELETE"
        "remove" | "JSON" | "incoming" | "DELETE"
    }

    protected CatalogueElement newResourceInstance() {
        resource.newInstance()
    }

    @Unroll
    def "#action with not-existing type to #direction endpoint with #format result"(){
        controller.request.method       = httpMethod
        controller.response.format = format.toLowerCase()
        def input = anotherLoadItem."encodeAs$format"()
        String fixtureName = "removeNonExisting${direction.capitalize()}$format"
        if(format=="JSON"){
            recordInputJSON fixtureName, input.toString()
        }
        if(format=="XML"){
            recordInputXML fixtureName, input
        }
        controller.request."${format.toLowerCase()}" = input
        controller."${action}${direction.capitalize()}"(loadItem.id, "no-such-type")

        expect:
        controller. response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        action   | format | direction  | httpMethod
        "add"    | "XML"  | "outgoing" | "POST"
        "add"    | "JSON" | "outgoing" | "POST"
        "add"    | "XML"  | "incoming" | "POST"
        "add"    | "JSON" | "incoming" | "POST"
        "remove" | "XML"  | "outgoing" | "DELETE"
        "remove" | "JSON" | "outgoing" | "DELETE"
        "remove" | "XML"  | "incoming" | "DELETE"
        "remove" | "JSON" | "incoming" | "DELETE"

    }


    def checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def method = incomingOrOutgoing
        if (incomingOrOutgoing == 'relationships') {
            incomingOrOutgoing = 'incoming'
        }
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)
        controller.response.format = "json"
        controller.params.offset = offset
        controller.params.id = first.id
        RelationshipType type2 = new RelationshipType(name: "xyz", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "xyz", destinationToSource: "zyx")
        assert type2.save()
        controller."${method}"(max, type2.name)
        JSONObject json = controller.response.json
        recordResult "${method}WithNonExistingType${no}", json
        assert json.success
        assert !json.list
        assert json.total == 0
        assert json.size == 0
        assert json.offset == offset
        assert json.page == max
        assert json.availableReports != null
        type2.delete()
        resource.count() == totalCount
    }

    def checkXmlRelationsInternal(typeParam, no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def method = incomingOrOutgoing
        if (incomingOrOutgoing == 'relationships') {
            incomingOrOutgoing = 'incoming'
        }
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)
        controller.response.format = "xml"
        controller.params.offset = offset
        controller.params.id = first.id
        controller."${method}"(max, typeParam)
        GPathResult result = controller.response.xml
        recordResult "${method}${no}", result
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
        assert item.@removeLink.text() ==  "/${GrailsNameUtils.getPropertyName(first.class)}/${first.id}/${incomingOrOutgoing}/relationship"
        def relation = Class.forName(item.relation.@elementType.text()).get(item.relation.@id.text() as Long)
        assert item.relation.name == relation.name
        assert item.relation.@id == "${relation.id}"
        resource.count() == totalCount
    }

    def checkJsonRelations(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal(null, no, size, max, offset, total, next, previous, incomingOrOutgoing)
        resource.count() == totalCount
    }

    def checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal("relationship", no, size, max, offset, total, next, previous, incomingOrOutgoing)
        resource.count() == totalCount
    }

    def checkXmlRelations(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkXmlRelationsInternal(null, no, size, max, offset, total, next, previous, incomingOrOutgoing)
        resource.count() == totalCount
    }

    def checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkXmlRelationsInternal("relationship", no, size, max, offset, total, next, previous, incomingOrOutgoing)
        resource.count() == totalCount
    }

    def checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def method = incomingOrOutgoing
        if (incomingOrOutgoing == 'relationships') {
            incomingOrOutgoing = 'incoming'
        }
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)
        controller.response.format = "xml"
        controller.params.offset = offset
        controller.params.id = first.id
        RelationshipType type2 = new RelationshipType(name: "xyz", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "xyz", destinationToSource: "zyx")
        assert type2.save()
        controller."${method}"(max, type2.name)
        GPathResult result = controller.response.xml
        recordResult "${method}WithNonExistingType${no}", result
        assert result
        assert result.@success.text() == "true"
        assert result.@total.text() == "0"
        assert result.@offset.text() == "${offset}"
        assert result.@page.text() == "${max}"
        assert result.@size.text() == "0"
        assert result.relationship.isEmpty()
        type2.delete()
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON for incoming relationships queried by type"() {
        controller.response.format = "json"
        controller.params.id = "1"
        controller.incoming(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for incoming relationships queried by type"() {
        controller.response.format = "xml"
        controller.params.id = "1"
        controller.incoming(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }


    def "Return 404 for non-existing item as JSON for combined relationships queried by type"() {
        controller.response.format = "json"
        controller.params.id = "1"
        controller.relationships(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for combined relationships queried by type"() {
        controller.response.format = "xml"
        controller.params.id = "1"
        controller.relationships(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON for outgoing relationships queried by type"() {
        controller.response.format = "json"
        controller.params.id = "1"
        controller.outgoing(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for outgoing relationships queried by type"() {
        controller.response.format = "xml"
        controller.params.id = "1"
        controller.outgoing(10, "no-such-type")

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON for combined relationships"() {
        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.relationships(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for combined relationships"() {
        controller.response.format = "xml"
        controller.params.id = "1000000"
        controller.relationships(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON for incoming relationships"() {
        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.incoming(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for incoming relationships"() {
        controller.response.format = "xml"
        controller.params.id = "1000000"
        controller.incoming(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON for outgoing relationships"() {
        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.outgoing(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML for outgoing relationships"() {
        controller.response.format = "xml"
        controller.params.id = "1000000"
        controller.outgoing(10, null)

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    @Unroll
    def "get json outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing")
    }

    @Unroll
    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming")
    }


    @Unroll
    def "get json combined relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships")
    }

    @Unroll
    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/relationship")
    }

    @Unroll
    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/relationship")
    }

    @Unroll
    def "get json combined relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/relationship")
    }


    @Unroll
    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/xyz")
    }

    @Unroll
    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/xyz")
    }


    @Unroll
    def "get json combined relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/xyz")
    }

    @Unroll
    def "get xml outgoing relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing")
    }

    @Unroll
    def "get xml incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming")
    }


    @Unroll
    def "get xml combined relationships pagination: #no where max: #max offset: #offset"() {
        checkXmlRelations(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships")
    }


    @Unroll
    def "get xml outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/relationship")
    }

    @Unroll
    def "get xml incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/relationship")
    }


    @Unroll
    def "get xml comibined relationships pagination with type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/relationship")
    }


    @Unroll
    def "get xml outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/xyz")
    }

    @Unroll
    def "get xml incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/xyz")
    }


    @Unroll
    def "get xml combined relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/xyz")
    }


    abstract Object getAnotherLoadItem()


    RelationshipType getRelationshipType(){
        RelationshipType.findByName("relationship")
    }



    protected linkRelationshipsToDummyEntities(String incomingOrOutgoing) {
        def first = loadItem
        first."${incomingOrOutgoing}Relationships" = first."${incomingOrOutgoing}Relationships" ?: []
        for (unit in resource.list()) {
            if (unit != first) {
                if (incomingOrOutgoing == "incoming") {
                    assert !controller.relationshipService.link(unit, first, relationshipType).hasErrors()
                } else {
                    assert !controller.relationshipService.link(first, unit, relationshipType).hasErrors()
                }
                if (first."${incomingOrOutgoing}Relationships".size() == 11) {
                    break
                }
            }
        }
        assert first."${incomingOrOutgoing}Relationships"
        assert first."${incomingOrOutgoing}Relationships".size() == 11
        first
    }


    private checkJsonRelationsInternal(typeParam, no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        def method = incomingOrOutgoing
        if (incomingOrOutgoing == 'relationships') {
            incomingOrOutgoing = 'incoming'
        }
        def first = linkRelationshipsToDummyEntities(incomingOrOutgoing)
        controller.response.format = "json"
        controller.params.offset = offset
        controller.params.id = first.id
        controller."${method}"(max, typeParam)
        JSONElement json = controller.response.json
        recordResult "${method}${no}", json
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        assert json.itemType == Relationship.name
        def item = json.list[0]
        assert item.type
        assert item.type.name == "relationship"
        assert item.type.sourceToDestination == "relates to"
        assert item.direction == incomingOrOutgoing == "incoming" ? "destinationToSource" : "sourceToDestination"
        assert item.type.destinationToSource == "is relationship of"
        assert item.relation
        assert item.relation.id
        assert item.relation.elementType
        assert item.removeLink == "/${GrailsNameUtils.getPropertyName(first.class)}/${first.id}/${incomingOrOutgoing}/relationship"
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

    @Override
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.name, item.name, "name")
        checkProperty(xml.description, item.description, "description")
        checkProperty(xml.@elementType, item.class.name, "elementType")
        checkProperty(xml.relationships.@count, (item?.incomingRelationships ? item.incomingRelationships.size(): 0) + (item?.outgoingRelationships ? item.outgoingRelationships.size(): 0), "relationships")
        checkProperty(xml.relationships.@link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/relationships", "relationships")
        checkProperty(xml.relationships.@itemType, Relationship.name, "itemType")
        checkProperty(xml.incomingRelationships.@count, (item?.incomingRelationships ? item.incomingRelationships.size(): 0) + (item?.outgoingRelationships ? item.outgoingRelationships.size(): 0), "incomingRelationships")
        checkProperty(xml.incomingRelationships.@link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/incoming", "incomingRelationships")
        checkProperty(xml.incomingRelationships.@itemType, Relationship.name, "itemType")
        checkProperty(xml.outgoingRelationships.@count, (item?.outgoingRelationships)?item.outgoingRelationships.size(): 0, "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/outgoing", "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@itemType, Relationship.name, "itemType")
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.name, inputItem.name, "name")
        checkProperty(xml.description, inputItem.description, "description")
        checkProperty(xml.@elementType, outputItem.class.name, "elementType")
        checkProperty(xml.relationships.@count, (outputItem?.incomingRelationships ? outputItem.incomingRelationships.size(): 0) + (outputItem?.outgoingRelationships ? outputItem.outgoingRelationships.size(): 0), "relationships")
        checkProperty(xml.relationships.@link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/relationships", "relationships")
        checkProperty(xml.relationships.@itemType, Relationship.name, "itemType")
        checkProperty(xml.incomingRelationships.@count, (outputItem?.incomingRelationships ? outputItem.incomingRelationships.size(): 0) + (outputItem?.outgoingRelationships ? outputItem.outgoingRelationships.size(): 0), "relationships")
        checkProperty(xml.incomingRelationships.@link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/incoming", "incomingRelationships")
        checkProperty(xml.incomingRelationships.@itemType, Relationship.name, "itemType")
        checkProperty(xml.outgoingRelationships.@count, (outputItem?.outgoingRelationships)?outputItem.outgoingRelationships.size(): 0, "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/outgoing", "outgoingRelationships")
        checkProperty(xml.outgoingRelationships.@itemType, Relationship.name, "itemType")
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkProperty(json.name , item.name, "name")
        checkProperty(json.description , item.description, "description")
        checkProperty(json.elementType , item.class.name, "elementType")
        checkProperty(json.relationships.count, (item?.incomingRelationships ? item.incomingRelationships.size(): 0) + (item?.outgoingRelationships ? item.outgoingRelationships.size(): 0), "relationshipsCount")
        checkProperty(json.relationships.link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/relationships", "relationshipsLink")
        checkProperty(json.relationships.itemType, Relationship.name, "relationshipsItemType")
        checkProperty(json.outgoingRelationships.count, (item?.outgoingRelationships)?item.outgoingRelationships.size(): 0, "outgoingCount")
        checkProperty(json.outgoingRelationships.link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/outgoing", "outgoingLink")
        checkProperty(json.outgoingRelationships.itemType, Relationship.name, "outgoingItemType")
        checkProperty(json.incomingRelationships.count, (item?.incomingRelationships)?item.incomingRelationships.size(): 0, "incomingCount")
        checkProperty(json.incomingRelationships.link, "/${GrailsNameUtils.getPropertyName(item.class.simpleName)}/${item.id}/incoming", "incomingLink")
        checkProperty(json.incomingRelationships.itemType, Relationship.name, "incomingItemType")

        assert json.dateCreated
        assert json.lastUpdated

        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.name , inputItem.name, "name")
        checkProperty(json.description , inputItem.description, "description")
        checkProperty(json.elementType , outputItem.class.name, "elementType")
        checkProperty(json.relationships.count, (outputItem?.incomingRelationships ? outputItem.incomingRelationships.size(): 0) + (outputItem?.outgoingRelationships ? outputItem.outgoingRelationships.size(): 0), "relationshipsCount")
        checkProperty(json.relationships.link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/relationships", "relationshipsLink")
        checkProperty(json.relationships.itemType, Relationship.name, "relationshipsItemType")
        checkProperty(json.outgoingRelationships.count, (outputItem?.outgoingRelationships)?outputItem.outgoingRelationships.size(): 0, "outgoingCount")
        checkProperty(json.outgoingRelationships.link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/outgoing", "outgoingLink")
        checkProperty(json.outgoingRelationships.itemType, Relationship.name, "outgoingItemType")
        checkProperty(json.incomingRelationships.count, (outputItem?.incomingRelationships)?outputItem.incomingRelationships.size(): 0, "incomingCount")
        checkProperty(json.incomingRelationships.link, "/${GrailsNameUtils.getPropertyName(outputItem.class.simpleName)}/${outputItem.id}/incoming", "incomingLink")
        checkProperty(json.incomingRelationships.itemType, Relationship.name, "incomingItemType")

        assert json.dateCreated
        assert json.lastUpdated

        return true
    }

    def getRelationshipPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 11, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 11, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 11, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 3, 4, 8, 11, "", "${baseLink}?max=4&offset=4"],
                [5, 1, 10, 10, 11, "", "${baseLink}?max=10&offset=0"],
                [6, 1, 2, 10, 11, "", "${baseLink}?max=2&offset=8"]
        ]
    }



    @Unroll
    def "get json mapping: #no where max: #max offset: #offset\""() {
        CatalogueElement first = loadItem
        mapToDummyEntities(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.mappings(max)
        def json = controller.response.json

        recordResult "mapping$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        json.itemType == Mapping.name

        when:
        def item  = json.list[0]
        def mapping = first.outgoingMappings.find {it.id == item.id}

        then:
        item.mapping == mapping.mapping
        item.destination
        item.destination.id == mapping.destination.id
        item.destination.elementType
        item.destination.elementType == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getMappingPaginationParameters("/${resourceName}/${loadItem.id}/mapping")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset"() {
        CatalogueElement first = loadItem
        mapToDummyEntities(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.mappings(max)
        def xml = controller.response.xml

        recordResult "mapping$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.mapping.size() == size


        when:
        def item  = xml.mapping[0]
        def mapping = first.outgoingMappings.find {it.id == item.@id.text() as Long}

        then:
        item.mapping.text() == mapping.mapping
        item.destination
        item.destination.@id.text() == "$mapping.destination.id"
        item.destination.@elementType
        item.destination.@elementType.text() == mapping.destination.class.name

        where:
        [no, size, max, offset, total, next, previous] << getMappingPaginationParameters("/${resourceName}/${loadItem.id}/mapping")
    }


    @Unroll
    def "return 404 for non existing domain calling #method method with #format format"() {
        controller.request.method = httpMethod
        controller.response.format = format
        controller.params.id = 1000000

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format | httpMethod | method
        "json" | "GET"      |"mappings"
        "xml"  | "GET"      |"mappings"
        "json" | "POST"      |"addMapping"
        "xml"  | "POST"      |"addMapping"
        "json" | "DELETE"      |"removeMapping"
        "xml"  | "DELETE"      |"removeMapping"
    }

    @Unroll
    def "return 404 for non existing other side calling #method method with #format format"() {
        controller.request.method = httpMethod
        controller.response.format = format
        controller.params.id = loadItem.id
        controller.params.destination = 10000000

        controller.request."$format" = payload

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format | httpMethod | method          | payload
        "json" | "POST"     | "addMapping"    | """{"mapping":"x"}"""
        "json" | "DELETE"   | "removeMapping" | """{"mapping":"x"}"""
        "xml"  | "POST"     | "addMapping"    | """<mapping>x</mapping>"""
        "xml"  | "DELETE"   | "removeMapping" | """<mapping>x</mapping>"""
    }

    @Unroll
    def "Map existing domains with failing constraint #format"(){
        controller.response.format = format
        controller.request."$format" = payload
        controller.request.method = "POST"
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def result = controller.response."$format"
        recordResult "addMappingFailed", result

        expect:
        controller.response.status == 422 // unprocessable entity
        test.call(result)

        where:
        format | payload                      | test
        "json" | """{"mapping":"y"}"""        | { it.errors && it.errors.first().field == "mapping" }
        "xml"  | """<mapping>y</mapping>"""   | { it.name() == "errors" && it.error[0].@field.text() == "mapping" }
    }

    @Unroll
    def "unmap non existing mapping will return 404 for #format request"(){
        controller.response.format = format
        controller.request.method = "DELETE"
        controller.mappingService.unmap(loadItem, anotherLoadItem)
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        format << ["json", "xml"]
    }


    @Unroll
    def "unmap existing mapping will return 204 for #format request"(){
        controller.response.format = format
        controller.request.method = "DELETE"
        controller.mappingService.map(loadItem, anotherLoadItem, [one: "one"])
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NO_CONTENT

        where:
        format << ["json", "xml"]
    }

    def "map valid domains with json"() {
        controller.request.method = "POST"
        controller.response.format = "json"
        controller.request.json = """{"mapping":"x"}"""
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def json = controller.response.json
        recordResult "addMapping", json

        expect:
        json.mapping            == "x"
        json.source
        json.source.id          == loadItem.id
        json.source.link        == loadItem.info.link
        json.destination
        json.destination.id     == anotherLoadItem.id
        json.destination.link   == anotherLoadItem.info.link
    }


    def "map valid domains with xml"() {
        controller.request.method = "POST"
        controller.response.format = "xml"
        controller.request.xml = """<mapping>x</mapping>"""
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def xml = controller.response.xml
        recordResult "addMapping", xml

        expect:
        xml.mapping.text()            == "x"
        xml.source
        xml.source.@id.text()         == "$loadItem.id"
        xml.source.link.text()        == loadItem.info.link
        xml.source.name.text()        == loadItem.name
        xml.destination
        xml.destination.@id.text()    == "$anotherLoadItem.id"
        xml.destination.link.text()   == anotherLoadItem.info.link
        xml.destination.name.text()   == anotherLoadItem.name
    }

    protected mapToDummyEntities(CatalogueElement toBeLinked) {
        for (domain in toBeLinked.class.list()) {
            if (domain != toBeLinked) {
                controller.mappingService.map(toBeLinked, domain, "x")
                if (toBeLinked.outgoingMappings.size() == 11) {
                    break
                }
            }
        }

        assert toBeLinked.outgoingMappings
        assert toBeLinked.outgoingMappings.size() == 11
        toBeLinked
    }


    def getMappingPaginationParameters(baseLink){
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 11, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 11, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 11, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 3, 4, 8, 11, "", "${baseLink}?max=4&offset=4"],
                [5, 1, 10, 10, 11, "", "${baseLink}?max=10&offset=0"],
                [6, 1, 2, 10, 11, "", "${baseLink}?max=2&offset=8"]
        ]

    }



}
