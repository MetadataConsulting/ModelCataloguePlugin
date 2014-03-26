package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesFactoryBean
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ResultRecorder
import org.modelcatalogue.core.util.marshalling.AbstractMarshallers
import org.modelcatalogue.core.util.marshalling.ElementsMarshaller
import org.modelcatalogue.fixtures.FixturesLoader
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Abstract parent for restful controllers specification.
 *
 * The concrete subclass must use {@link grails.test.mixin.web.ControllerUnitTestMixin}.
 * The concrete subclass must use @Mixin(ResultRecorder)
 *
 */

abstract class AbstractRestfulControllerSpec<T> extends Specification implements ResultRecorder {

    private static final int DUMMY_ENTITIES_COUNT = 12

    def newInstance, badInstance, propertiesToEdit, loadItem2, loadItem1
    String badXmlCreateError = "Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
    String badXmlUpdateError = "Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"


    FixturesLoader fixturesLoader = new FixturesLoader("../ModelCatalogueCorePlugin/fixtures")
    ResultRecorder recorder

    def setup() {
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                resourceName
        )
        setupMimeTypes()
        [marshallers, [new ElementsMarshaller()]].flatten().each {
            it.register()
        }

    }

    def cleanup() {
        resource.deleteAll(resource.list())
    }


    protected void setupMimeTypes() {
        def ga = grailsApplication
        ga.config.grails.mime.types =
                [html: ['text/html', 'application/xhtml+xml'],
                        xml: ['text/xml', 'application/xml'],
                        text: 'text/plain',
                        js: 'text/javascript',
                        rss: 'application/rss+xml',
                        atom: 'application/atom+xml',
                        css: 'text/css',
                        csv: 'text/csv',
                        all: '*/*',
                        json: ['application/json', 'text/json'],
                        form: 'application/x-www-form-urlencoded',
                        multipartForm: 'multipart/form-data'
                ]

        defineBeans {
            mimeTypes(MimeTypesFactoryBean) {
                grailsApplication = ga
            }
        }
    }


    @Unroll
    def "list json items test: #no where max: #max offset: #offset"() {
        fillWithDummyEntities()

        expect:
        resource.count() == total


        when:


        response.format = "json"
        params.max = max
        params.offset = offset

        controller.index()
        JSONElement json = response.json

        String list = "list${no}"


        recordResult list, json

        then:

        json.success
        json.size == size
        json.total == total
        json.offset == offset
        json.page == max
        json.list
        json.list.size() == size
        json.next == next
        json.previous == previous
        json.listType == Elements.name
        json.itemType == resource.name



        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/")
    }

    @Unroll
    def "list xml items test: #no where max: #max offset: #offset"() {
        fillWithDummyEntities()

        expect:
        resource.count() == total


        when:
        response.format = "xml"
        params.max = max
        params.offset = offset

        controller.index()
        GPathResult xml = response.xml

        String list = "list${no}"
        recordResult list, xml

        then:

        xml.@success.text() == "true"
        xml.@size.text() == "${size}"
        xml.@total.text() == "${total}"
        xml.@offset.text() == "${offset}"
        xml.@page.text() == "${max}"
        xml.element
        xml.element.size() == size
        xml.next.text() == next
        xml.previous.text() == previous



        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/")
    }



    def "Show single existing item as JSON"() {

        when:
        response.format = "json"

        params.id = "${loadItem1.id}"

        controller.show()

        JSONObject json = response.json

        recordResult 'showOne', json

        then:
        json
        json.link == "/${resourceName}/1"

        customJsonPropertyCheck loadItem1, json

    }




    def "Show single existing item as XML"() {
        response.format = "xml"
        params.id = "${loadItem1.id}"
        controller.show()
        GPathResult xml = response.xml
        recordResult "showOne", xml

        expect:
        xml
        xml.@link
        xml.@link.text() == "/${resourceName}/1"
        xmlCustomPropertyCheck xml, loadItem1

    }


    @Unroll
    def "Do not #action new instance from JSON with bad json name"() {
        expect:
        !resource.findByName(badInstance.name)

        when:
        response.format = "json"
        request.json = badInstance.properties
        controller."$action"()
        JSONElement created = response.json
        def stored = resource.findByName(badInstance.name)
        recordResult action + 'Errors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() >= 1
        created.errors.first().field == "name"

        where:
        action << ['save', 'validate']
    }

    def "Create new instance from JSON"() {
        expect:
        !resource.findByName(newInstance.name)

        when:
        response.format = "json"
        def json = newInstance.properties
        request.json = json
        recordInputJSON 'createInput', json
        controller.save()
        JSONObject created = response.json
        def stored = resource.findByName(newInstance.name)
        recordResult 'saveOk', created

        then:
        stored
        created
        customJsonPropertyCheck newInstance, created, stored

    }


    def "Create new instance from XML"() {
        expect:
        !resource.findByName(newInstance.name)

        when:
        response.format = "xml"
        def xml = newInstance.encodeAsXML()
        recordInputXML "createInput", xml
        request.xml = xml
        controller.save()
        GPathResult created = response.xml
        def stored = resource.findByName(newInstance.name)
        recordResult "createOk", created

        then:
        stored
        created
        created.@id == stored.id
        created.@version == stored.version

        xmlCustomPropertyCheck newInstance, created, stored


    }

    def "edit instance description from JSON"() {

        def json = propertiesToEdit
        def properties = new HashMap()
        properties.putAll(loadItem1.properties)
        def instance = resource.newInstance(properties)
        instance.properties = propertiesToEdit


        expect:
        instance
        loadItem1
        json

        when:
        response.format = "json"
        params.id = loadItem1.id

        request.json = json


        recordInputJSON 'updateInput', json

        controller.update()

        JSONObject updated = response.json

        recordResult 'updateOk', updated

        then:
        updated
        customJsonPropertyCheck instance, updated, loadItem1

    }

    def "edit instance from XML"() {

        def properties = new HashMap()
        properties.putAll(loadItem1.properties)
        def instance = resource.newInstance(properties)
        instance.properties = propertiesToEdit

        expect:
        instance
        loadItem1

        when:

        response.format = "xml"
        params.id = loadItem1.id

        def xml = instance.encodeAsXML()
        request.xml = xml

        recordInputXML "updateInput", xml

        controller.update()

        GPathResult updated = response.xml

        recordResult 'updateOk', updated

        then:
        updated
        xmlCustomPropertyCheck instance, updated, loadItem1

    }

    def "Do not create new instance with bad XML"() {
        expect:
        !resource.findByName("")

        when:
        response.format = "xml"
        def xml = badInstance.encodeAsXML()
        request.xml = xml

        recordInputXML action + "ErrorsInput", xml

        controller."$action"()

        GPathResult created = response.xml
        def stored = resource.findByName("")

        recordResult action + 'Errors', created

        then:
        !stored
        created
        created.depthFirst().find { it.@field == 'name'} == badXmlCreateError

        where:
        action << ['save', 'validate']
    }

    def "edit instance with bad JSON name"() {
        def instance = resource.findByName(loadItem1.name)

        expect:
        instance

        when:
        response.format = "json"
        params.id = instance.id
        request.json = [name: "g" * 256]

        controller.update()

        JSONObject updated = response.json

        recordResult 'updateErrors', updated

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'


    }

    def "edit instance with bad XML"() {
        def instance = resource.findByName(loadItem1.name)

        expect:
        instance

        when:
        response.format = "xml"
        params.id = instance.id
        def xml = badInstance.encodeAsXML()

        recordInputXML "updateErrorsInput", xml

        request.xml = xml

        controller.update()

        GPathResult updated = response.xml

        recordResult 'updateErrors', updated

        then:
        updated
        updated.depthFirst().find { it.@field == 'name'} == badXmlUpdateError

    }

    def "Return 404 for non-existing item as JSON"() {
        response.format = "json"

        params.id = "1000000"

        controller.show()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML"() {
        response.format = "xml"

        params.id = "1000000"

        controller.show()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }


    def "Return 404 for non-existing item as JSON on delete"() {
        response.format = "json"

        params.id = "1000000"

        controller.delete()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }

    def "Return 404 for non-existing item as XML on delete"() {
        response.format = "xml"

        params.id = "1000000"

        controller.delete()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NOT_FOUND
    }


    def "Return 204 for existing item as JSON on delete"() {
        response.format = "json"

        params.id = "1"

        controller.delete()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NO_CONTENT
        !resource.get(params.id)
    }

    def "Return 204 for existing item as XML on delete"() {
        response.format = "xml"

        params.id = "1"

        controller.delete()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NO_CONTENT
        !resource.get(params.id)
    }


//    boolean jsonPropertyCheck(json, loadItem) {
//        for (int j = 0; (j < propertiesToCheck.size()); j++) {
//            def property = propertiesToCheck[j]
//            property = property.toString().replaceAll("\\@", "")
//            def subProperties = property.split("\\.")
//            def jsonProp = json
//            def loadProp = loadItem
//
//            if (subProperties.size() > 1) {
//                for (int i = 0; (i < subProperties.size()); i++) {
//                    def subProperty = subProperties[i]
//                    jsonProp = jsonProp[subProperty]
//                    loadProp = loadProp.getProperty(subProperty)
//                }
//            } else {
//                jsonProp = json[property]
//                loadProp = loadItem.getProperty(property)
//            }
//
//            def jsonMap = JSON.parse(jsonProp.toString())
//            if (jsonMap) {
//                jsonProp = JSON.parse(jsonProp.toString())
//            } else {
//                jsonProp = (jsonProp.toString()!="{}")?:null
//                loadProp = (loadProp.toString()!="[:]")?:null
//            }
//
//            if (jsonProp != loadProp) {
//                throw new AssertionError("error: property to check: ${propertiesToCheck[j]}  where json:${jsonProp} !=  item:${loadProp}")
//            }
//        }
//
//        return true
//
//    }
//
//
//    boolean xmlPropertyCheck(xml, loadItem) {
//        for (int j = 0; (j < propertiesToCheck.size()); j++) {
//            def property = propertiesToCheck[j]
//            def subProperties = property.toString().split("\\.")
//            def xmlProp = xml
//            def loadProp = loadItem
//
//            if (subProperties.size() > 1) {
//                for (int i = 0; (i < subProperties.size()); i++) {
//                    def subProperty = subProperties[i]
//                    if (subProperty.contains("@")) {
//                        xmlProp = xmlProp[subProperty]
//                        subProperty = subProperty.replaceAll("\\@", "")
//                        loadProp = loadProp.getProperty(subProperty)
//                    } else {
//                        xmlProp = xmlProp[subProperty]
//                        loadProp = loadProp.getProperty(subProperty)
//                    }
//                }
//            } else {
//                xmlProp = (xml[property].toString()) ?: null
//                loadProp = loadItem.getProperty(property.toString().replaceAll("\\@", ""))
//            }
//
//            if (xmlProp.toString() != loadProp.toString()) {
//                throw new AssertionError("error: property to check: ${propertiesToCheck[j]}  where xml:${xmlProp} !=  item:${loadProp}")
//            }
//        }
//        return true
//
//    }
//

    void fillWithDummyEntities(int limit = DUMMY_ENTITIES_COUNT) {
        if (limit <= resource.count()) return
        (resource.count() + 1).upto(limit) {
            assert resource.newInstance(getUniqueDummyConstructorArgs(it)).save()
        }
    }

    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "$resourceName $counter"]
    }

    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerSpec")
    }

    List<AbstractMarshallers> getMarshallers() { [] }

    abstract Class<T> getResource()


    protected RelationshipType prepareTypeAndDummyEntities() {
        fixturesLoader.load('relationshipTypes/RT_relationship')
        fillWithDummyEntities(15)
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        assert relationshipType
        relationshipType
    }


    protected static checkXmlCorrectListValues(GPathResult xml, total, size, offset, max, next, previous) {
        assert xml.@success.text() == "true"
        assert xml.@total.text() == "${total}"
        assert xml.@size.text() == "${size}"
        assert xml.@offset.text() == "${offset}"
        assert xml.@page.text() == "${max}"
        assert xml.next.text() == next
        assert xml.previous.text() == previous
        true
    }

    protected static getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 12, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 12, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 10, 10, 12, "", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, 12, "", "${baseLink}?max=2&offset=8"]
        ]
    }

    def xmlCustomPropertyCheck(xml, item){
        checkProperty(xml.@id, item.id, "id")
        checkProperty(xml.@version, item.version, "version")
        //xmlPropertyCheck xml, item
        return true
    }

    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        checkProperty(xml.@id, outputItem.id, "id")
        checkProperty(xml.@version, outputItem.version, "version")
        //xmlPropertyCheck outputItem, xml
        return true
    }


    def customJsonPropertyCheck(item, json){
        checkProperty(json.id , item.id, "id")
        checkProperty(json.version , item.version, "version")
        //jsonPropertyCheck json, item
        return true
    }


    def customJsonPropertyCheck(inputItem, json, outputItem){
        checkProperty(json.id , outputItem.id, "id")
        checkProperty(json.version , outputItem.version, "version")
        //jsonPropertyCheck json, outputItem
        return true
    }


    def checkProperty(property, value, propertyName){
        property = property.toString()
        value = value.toString()
        if(property!= value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkProperty(JSONObject property, value, propertyName){
        if (property != value) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkProperty(Map property, Map value, propertyName){
        if (property != value) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    @Override
    File recordResult(String fixtureName, JSONElement json) {
        recorder.recordResult(fixtureName, json)
    }

    @Override
    File recordResult(String fixtureName, GPathResult xml) {
        recorder.recordResult(fixtureName, xml)
    }

    @Override
    File recordInputJSON(String fixtureName, Map json) {
        recorder.recordInputJSON(fixtureName, json)
    }

    @Override
    File recordInputJSON(String fixtureName, String json) {
        recorder.recordInputJSON(fixtureName, json)
    }

    @Override
    File recordInputXML(String fixtureName, String xml) {
        recorder.recordInputXML(fixtureName, xml)
    }
}
