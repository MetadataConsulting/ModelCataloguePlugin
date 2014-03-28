package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.util.ValueDomains
import org.modelcatalogue.core.util.marshalling.AbstractMarshallers
import org.modelcatalogue.core.util.marshalling.EnumeratedTypeMarshaller
import org.modelcatalogue.core.util.marshalling.ValueDomainsMarshaller
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(EnumeratedTypeController)
@Mock([EnumeratedType, Relationship, RelationshipType, ValueDomain])
class EnumeratedTypeControllerSpec extends CatalogueElementRestfulControllerSpec {

    RelationshipType type

    def setup() {
        new EnumeratedTypeMarshaller().register()
        fixturesLoader.load('enumeratedTypes/ET_schoolSubjects', 'enumeratedTypes/ET_uniSubjects', 'enumeratedTypes/ET_uni2Subjects', 'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.ET_schoolSubjects.save())
        assert (loadItem2 = fixturesLoader.ET_uniSubjects.save())
        assert (type = fixturesLoader.RT_relationship.save())
        assert !(new RelationshipService().link(loadItem1, loadItem2, type).hasErrors())

        //configuration properties for abstract controller
        assert (newInstance = new EnumeratedType(name: "sub4", enumerations: [h1: 'history', p1: 'politics', sci1: 'science']))
        assert (badInstance = new EnumeratedType(name: "", description: "asdf"))
        assert (propertiesToEdit = [description: "edited description ", enumerations: ["T1": 'test1', "T2": 'test2', "T3": 'test3']])
        //assert (propertiesToCheck = ['name', 'description', 'enumerations'])

    }

    def cleanup() {
        type.delete()
    }

    def xmlCustomPropertyCheck(xml, item){

        super.xmlCustomPropertyCheck(xml, item)

        def xmlProp = xml.depthFirst().find { it.name() == "enumerations" }
        if (xmlProp) {
            xmlProp = xmlProp.attributes()
            checkProperty(xmlProp, item.getProperty("enumerations"), "enumerations")
        }

        return true
    }

    def xmlCustomPropertyCheck(inputItem, xml, outputItem){

        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        def xmlProp = xml.depthFirst().find { it.name() == "enumerations" }
        if (xmlProp) {
            xmlProp = xmlProp.attributes()
            checkProperty(xmlProp, inputItem.getProperty("enumerations"), "enumerations")
        }

        return true
    }


    def customJsonPropertyCheck(item, json){

        super.customJsonPropertyCheck(item, json)
        checkProperty(json.enumerations , item.enumerations, "enumerations")

        return true
    }


    def customJsonPropertyCheck(inputItem, json, outputItem){

        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.enumerations , inputItem.enumerations, "enumerations")
        return true

    }



    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "ENumeratedType${counter}", enumerations: ["H${counter}": "history${counter}", "P${counter}": "politics${counter}", "SCI${counter}": "science${counter}", "GEO${counter}": "geography${counter}"]]
    }

    Class getResource() {
        EnumeratedType
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new EnumeratedTypeMarshaller(), new ValueDomainsMarshaller()]
    }

    def createValueDomainsUsingEnumeratedType(EnumeratedType enumeratedType, Integer max){
        max.times {new ValueDomain(name: "dataTypeValueDomain${it}", description: "the ground speed of the moving vehicle", dataType: enumeratedType).save()}
    }



    @Unroll
    def "get json valueDomains: #no where max: #max offset: #offset\""() {
        fillWithDummyEntities(1)
        EnumeratedType first = EnumeratedType.get(1)
        createValueDomainsUsingEnumeratedType(first, 12)

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
        EnumeratedType first = EnumeratedType.get(1)
        createValueDomainsUsingEnumeratedType(first, 12)

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

//    @Unroll
//    def "get json outgoing relationships pagination: #no where max: #max offset: #offset"() {
//        checkJsonRelations(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
//    }
//
//    @Unroll
//    def "get json incoming relationships pagination: #no where max: #max offset: #offset"() {
//        checkJsonRelations(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
//    }
//
//
//    @Unroll
//    def "get json outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
//        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
//    }
//
//    @Unroll
//    def "get json incoming relationships pagination with type: #no where max: #max offset: #offset"() {
//        checkJsonRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
//    }
//
//
//    @Unroll
//    def "get json outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
//        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
//    }
//
//    @Unroll
//    def "get json incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
//        checkJsonRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
//    }
//
//    @Unroll
//    def "get xml outgoing relationships pagination: #no where max: #max offset: #offset"() {
//        checkXmlRelations(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing")
//    }
//
//    @Unroll
//    def "get xml incoming relationships pagination: #no where max: #max offset: #offset"() {
//        checkXmlRelations(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming")
//    }
//
//
//    @Unroll
//    def "get xml outgoing relationships pagination with type: #no where max: #max offset: #offset"() {
//        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/relationship")
//    }
//
//    @Unroll
//    def "get xml incoming relationships pagination with type: #no where max: #max offset: #offset"() {
//        checkXmlRelationsWithRightType(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/relationship")
//    }
//
//
//    @Unroll
//    def "get xml outgoing relationships pagination with wrong type: #no where max: #max offset: #offset"() {
//        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "outgoing")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/outgoing/xyz")
//    }
//
//    @Unroll
//    def "get xml incoming relationships pagination with wrong type: #no where max: #max offset: #offset"() {
//        checkXmlRelationsWithWrongType(no, size, max, offset, total, next, previous, "incoming")
//
//        cleanup:
//        RelationshipType.findByName("relationship")?.delete()
//
//        where:
//        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/1/incoming/xyz")
//    }

    // -- end copy and pasted
}


