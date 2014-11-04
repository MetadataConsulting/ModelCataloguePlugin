package org.modelcatalogue.core

import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
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
        json.element
        json.element.id  == anotherLoadItem.id
        json.relation
        json.relation.id == loadItem.id
        json.type
        json.type.id     == relationshipType.id
        resource.count() == totalCount

        where:
        direction << ["incoming", "outgoing"]
    }

    @Unroll
    def "Unlink non existing elements using add to #direction endpoint with json result"(){

        controller.request.method       = 'DELETE'
        if (direction == "outgoing") {
            controller.relationshipService.unlink(loadItem, anotherLoadItem, relationshipType)
        } else {
            controller.relationshipService.unlink(anotherLoadItem, loadItem, relationshipType)
        }
        controller.response.format = 'json'
        def input = anotherLoadItem.encodeAsJSON()
        String fixtureName = "removeNonExisting${direction.capitalize()}JSON"
        recordInputJSON fixtureName, input.toString()
        controller.request.json = input
        controller."remove${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        _ | direction
        _ | "outgoing"
        _ | "incoming"
    }

    @Unroll
    def "Unlink existing elements using add to #direction endpoint with json result"(){
        controller.request.method       = 'DELETE'
        if (direction == "outgoing") {
            controller.relationshipService.link(loadItem, anotherLoadItem, relationshipType)
        } else {
            controller.relationshipService.link(anotherLoadItem, loadItem, relationshipType)
        }
        controller.response.format = 'json'
        def input = anotherLoadItem.encodeAsJSON()
        String fixtureName = "removeNonExisting${direction.capitalize()}json"
        recordInputJSON fixtureName, input.toString()
        controller.request.json = input
        controller."remove${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NO_CONTENT
        resource.count() == totalCount

        where:
        _ | direction
        _ | "outgoing"
        _ | "incoming"
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
    def "#action with non-existing one to #direction endpoint with json result"(){
        controller.request.method       = method
        controller.response.format = 'json'
        def item = newResourceInstance()
        item.id = 1000000
        def input = item.encodeAsJSON()
        String fixtureName = "removeNonExisting${direction.capitalize()}JSON"
        recordInputJSON fixtureName, input.toString()
        controller.request.json = input
        controller."${action}${direction.capitalize()}"(loadItem.id, relationshipType.name)

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        action   | direction  | method
        "add"    | "outgoing" | "POST"
        "add"    | "outgoing" | "POST"
        "add"    | "incoming" | "POST"
        "add"    | "incoming" | "POST"
        "remove" | "outgoing" | "DELETE"
        "remove" | "outgoing" | "DELETE"
        "remove" | "incoming" | "DELETE"
        "remove" | "incoming" | "DELETE"
    }

    protected CatalogueElement newResourceInstance() {
        resource.newInstance()
    }

    @Unroll
    def "#action with not-existing type to #direction endpoint with json result"(){
        controller.request.method = httpMethod
        controller.response.format = 'json'
        def input = anotherLoadItem.encodeAsJSON()
        String fixtureName = "removeNonExisting${direction.capitalize()}JSON"
        recordInputJSON fixtureName, input.toString()
        controller.request.json = input
        controller."${action}${direction.capitalize()}"(loadItem.id, "no-such-type")

        expect:
        controller. response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount

        where:
        action   | direction  | httpMethod
        "add"    | "outgoing" | "POST"
        "add"    | "outgoing" | "POST"
        "add"    | "incoming" | "POST"
        "add"    | "incoming" | "POST"
        "remove" | "outgoing" | "DELETE"
        "remove" | "outgoing" | "DELETE"
        "remove" | "incoming" | "DELETE"
        "remove" | "incoming" | "DELETE"

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


    def checkJsonRelations(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal(null, no, size, max, offset, total, next, previous, incomingOrOutgoing)
        resource.count() == totalCount
    }

    def checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, incomingOrOutgoing) {
        checkJsonRelationsInternal("relationship", no, size, max, offset, total, next, previous, incomingOrOutgoing)
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


    def "Return 404 for non-existing item as JSON for combined relationships queried by type"() {
        controller.response.format = "json"
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

    def "Return 404 for non-existing item as JSON for combined relationships"() {
        controller.response.format = "json"
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

    def "Return 404 for non-existing item as JSON for outgoing relationships"() {
        controller.response.format = "json"
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
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing"))
    }

    @Unroll
    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming"))
    }


    @Unroll
    def "get json combined relationships pagination: #no where max: #max offset: #offset"() {
        checkJsonRelations(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships"))
    }

    @Unroll
    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/relationship"))
    }

    @Unroll
    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/relationship"))
    }

    @Unroll
    def "get json combined relationships pagination with type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/relationship"))
    }


    @Unroll
    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/outgoing/xyz"))
    }

    @Unroll
    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/incoming/xyz"))
    }


    @Unroll
    def "get json combined relationships pagination with wrong type: #no where max: #max offset: #offset"() {
        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "relationships")

        expect:
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getRelationshipPaginationParameters("/${resourceName}/${loadItem.id}/relationships/xyz"))
    }

    abstract Object getAnotherLoadItem()


    RelationshipType getRelationshipType(){
        RelationshipType.relationshipType
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
        [no, size, max, offset, total, next, previous] << optimize(getMappingPaginationParameters("/${resourceName}/${loadItem.id}/mapping"))
    }


    @Unroll
    def "return 404 for non existing domain calling #method method with json format"() {
        controller.request.method = httpMethod
        controller.response.format = 'json'
        controller.params.id = 1000000

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        httpMethod  | method
        "GET"       |"mappings"
        "POST"      |"addMapping"
        "DELETE"    |"removeMapping"
    }

    @Unroll
    def "return 404 for non existing other side calling #method method with json format"() {
        controller.request.method = httpMethod
        controller.response.format = 'json'
        controller.params.id = loadItem.id
        controller.params.destination = 10000000

        controller.request.json = payload

        when:
        controller."$method"()

        then:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND

        where:
        httpMethod | method          | payload
        "POST"     | "addMapping"    | """{"mapping":"x"}"""
        "DELETE"   | "removeMapping" | """{"mapping":"x"}"""
    }

    @Unroll
    def "Map existing domains with failing constraint json"(){
        controller.response.format = 'json'
        controller.request.json = payload
        controller.request.method = "POST"
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.addMapping()
        def result = controller.response.json
        recordResult "addMappingFailed", result

        expect:
        controller.response.status == 422 // unprocessable entity
        test.call(result)

        where:
        payload                      | test
        """{"mapping":"y"}"""        | { it.errors && it.errors.first().field == "mapping" }
    }

    @Unroll
    def "unmap non existing mapping will return 404 for json request"(){
        controller.response.format = 'json'
        controller.request.method = "DELETE"
        controller.mappingService.unmap(loadItem, anotherLoadItem)
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
    }


    @Unroll
    def "unmap existing mapping will return 204 for json request"(){
        controller.response.format = 'json'
        controller.request.method = "DELETE"
        controller.mappingService.map(loadItem, anotherLoadItem, [one: "one"])
        controller.params.id           = loadItem.id
        controller.params.destination  = anotherLoadItem.id
        controller.removeMapping()

        expect:
        controller.response.status == HttpServletResponse.SC_NO_CONTENT
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


    def "update and set metadata"() {
        if (controller.readOnly) return
        CatalogueElement another        = CatalogueElement.get(anotherLoadItem.id)
        String newName                  = "UPDATED NAME"
        Map keyValue = new HashMap()
        keyValue.put('testKey', 'testValue')

        when:
        controller.request.method       = 'PUT'
        controller.params.id            = another.id
        controller.params.newVersion    = true
        controller.request.json         = [name: newName, ext: keyValue]
        controller.response.format      = "json"

        controller.update()
        def json = controller.response.json

        then:
        json.name                   == newName
        json.ext == keyValue

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
        ]

    }



}
