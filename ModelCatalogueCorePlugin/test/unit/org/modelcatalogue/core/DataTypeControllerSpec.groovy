package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.Mappings
import org.modelcatalogue.core.util.ValueDomains
import org.modelcatalogue.core.util.marshalling.AbstractMarshallers
import org.modelcatalogue.core.util.marshalling.DataTypeMarshaller
import org.modelcatalogue.core.util.marshalling.ValueDomainsMarshaller
import spock.lang.IgnoreIf
import spock.lang.Unroll

import javax.lang.model.element.Element

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataTypeController)
@Mock([DataType, Relationship, RelationshipType, Model, ValueDomain])
class DataTypeControllerSpec extends CatalogueElementRestfulControllerSpec {

    RelationshipType type

    def setup() {
        fixturesLoader.load('dataTypes/DT_integer', 'dataTypes/DT_double', 'dataTypes/DT_string', 'relationshipTypes/RT_relationship')
        assert (loadItem1 = fixturesLoader.DT_integer.save())
        assert (loadItem2 = fixturesLoader.DT_double.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !(new RelationshipService().link(loadItem1, loadItem2, type).hasErrors())
        assert (newInstance = fixturesLoader.DT_string)
        assert (badInstance = new DataType(name: "", description: "asdf"))
        assert (propertiesToEdit = [description: "edited description "])
    }

    def cleanup() {
        RelationshipType.deleteAll(RelationshipType.list())
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new DataTypeMarshaller(), new ValueDomainsMarshaller()]
    }


    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "Concept${counter}", description: "the conceptual domain"]
    }

    Class getResource() {
        DataType
    }


    def createValueDomainsUsingDataType(DataType dataType, Integer max){
        max.times {new ValueDomain(name: "dataTypeValueDomain${it}", description: "the ground speed of the moving vehicle", dataType: dataType).save()}
    }



    @Unroll
    def "get json valueDomains: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(1)
        DataType first = DataType.get(1)
        createValueDomainsUsingDataType(first, 12)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "json"
        controller.valueDomains(max)
        def json = response.json

        recordResult "valueDomains$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        json.listType == ValueDomains.name
        //json.itemType == ValueDomain.name

        when:
        def item  = json.list[0]
        def valueDomain = first.valueDomains.find {it.id == item.id}

        then:
        item.id == valueDomain.id
        item.dataType.id == valueDomain.dataType.id

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/valueDomain")
    }


    @Unroll
    def "get xml mapping: #no where max: #max offset: #offset"() {
        fillWithDummyEntities(1)
        DataType first = DataType.get(1)
        createValueDomainsUsingDataType(first, 12)

        when:
        params.id = first.id
        params.offset = offset
        params.max = max
        response.format = "xml"
        controller.valueDomains(max)
        def xml = response.xml

        recordResult "valueDomains$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.valueDomain.size() == size


        when:
        def item  = xml.valueDomain[0]
        def valueDomain = first.valueDomains.find {it.id == item.@id.text() as Long}

        then:

        item.@id == valueDomain.id
        item.dataType.@id == valueDomain.dataType.id


        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/valueDomain")
    }



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

}


