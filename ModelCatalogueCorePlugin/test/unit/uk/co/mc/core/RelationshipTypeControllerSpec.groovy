package uk.co.mc.core

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesFactoryBean
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mc.core.util.ResultRecorder
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.ElementsMarshaller
import uk.co.mc.core.util.marshalling.RelationshipMarshallers
import uk.co.mc.core.util.marshalling.RelationshipTypeMarshaller
import uk.co.mc.core.util.marshalling.RelationshipsMarshaller

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RelationshipTypeController)
@Mock([RelationshipType, Relationship])
@Mixin(ResultRecorder)
class RelationshipTypeControllerSpec extends Specification {

    def controllerName, relClass
    
    def setup() {
        new RelationshipTypeMarshaller().register()

        RelationshipType.initDefaultRelationshipTypes()

        setupMimeTypes()
        controllerName = controller.resourceName
        relClass = "RelationshipType"


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

    def cleanup() {
    }


    @Unroll
    def "list json items test: #no where max: #max offset: #offset"() {

        expect:
        RelationshipType.count() == total


        when:
        response.format = "json"
        params.max = max
        params.offset = offset

        controller.index()
        JSONElement json = response.json

        String list = "list${no}"

        recordResult list, json, controllerName, relClass

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

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/RelationshipType/")
    }

    @Unroll
    def "list xml items test: #no where max: #max offset: #offset"() {

        expect:
        RelationshipType.count() == total


        when:
        response.format = "xml"
        params.max = max
        params.offset = offset

        controller.index()
        GPathResult xml = response.xml

        String list = "list${no}"

        recordResult list, xml, controllerName

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
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/RelationshipType/")
    }



    def "Show single existing item as JSON"() {

        when:
        response.format = "json"

        params.id = "${loadItem1.id}"

        controller.show()

        JSONObject json = response.json

        recordResult 'showOne', json, controllerName, relClass

        then:
        json
        json.id == loadItem1.id
        json.version == loadItem1.version
        json.elementType == loadItem1.class.name
        json.elementTypeName == GrailsNameUtils.getNaturalName(loadItem1.class.simpleName)
        json.outgoingRelationships == [count: 1, link: "/RelationshipType/outgoing/${loadItem1.id}"]
        json.incomingRelationships == [count: 0, link: "/RelationshipType/incoming/${loadItem1.id}"]

        jsonPropertyCheck(json, loadItem1)


    }


    def "Do not create new instance from JSON with bad json name"() {
        expect:
        !RelationshipType.findByName("b" * 256)

        when:
        response.format = "json"
        request.json = badInstance.properties

        controller.save()

        JSONElement created = response.json
        def stored = RelationshipType.findByName("b" * 256)

        recordResult 'saveErrors', created, controllerName, relClass

        then:
        !stored
        created
        created.errors
        created.errors.size() >= 1
        created.errors.first().field == 'name'
    }

    def "Create new instance from JSON"() {
        expect:
        !RelationshipType.findByName(newInstance.name)

        when:
        response.format = "json"

        def json = newInstance.properties
        request.json = json


        recordInputJSON 'createInput', json, controllerName, getClass


        controller.save()

        JSONObject created = response.json
        def stored = RelationshipType.findByName(newInstance.name)

        recordResult 'saveOk', created, controllerName, relClass

        then:
        stored
        created
        created.id == stored.id
        created.version == stored.version
        jsonPropertyCheck(created, newInstance)
    }


    def "Create new instance from XML"() {
        expect:
        !RelationshipType.findByName(newInstance.name)

        when:
        response.format = "xml"

        def xml = newInstance.encodeAsXML()

        recordInputXML "createInput", xml, controllerName

        request.xml = xml

        controller.save()

        GPathResult created = response.xml
        def stored = RelationshipType.findByName(newInstance.name)

        recordResult "createOk", created, controllerName

        then:
        stored
        created
        created.@id == stored.id
        created.@version == stored.version

        xmlPropertyCheck(created, newInstance)

    }


    def "Show single existing item as XML"() {
        response.format = "xml"

        params.id = "${loadItem1.id}"

        controller.show()

        GPathResult xml = response.xml
        recordResult "showOne", xml, controllerName

        expect:
        xml
        xml.@id == loadItem1.id
        xml.@version == loadItem1.version
        xml.@elementType == loadItem1.class.name
        xml.@elementTypeName == GrailsNameUtils.getNaturalName(loadItem1.class.simpleName)

        xmlPropertyCheck(xml, loadItem1)

    }

    def "edit instance description from JSON"() {

        def json = propertiesToEdit
        def properties = new HashMap()
        properties.putAll(loadItem1.properties)
        def instance = RelationshipType.newInstance(properties)
        instance.properties = propertiesToEdit


        expect:
        instance
        loadItem1
        json

        when:
        response.format = "json"
        params.id = loadItem1.id

        request.json = json


        recordInputJSON 'updateInput', json, controllerName

        controller.update()

        JSONObject updated = response.json

        recordResult 'updateOk', updated, controllerName, relClass

        then:
        updated
        updated.id == loadItem1.id
        updated.version == loadItem1.version
        jsonPropertyCheck(updated, instance)

    }

    def "edit instance from XML"() {

        def properties = new HashMap()
        properties.putAll(loadItem1.properties)
        def instance = RelationshipType.newInstance(properties)
        instance.properties = propertiesToEdit

        expect:
        instance
        loadItem1

        when:

        response.format = "xml"
        params.id = loadItem1.id

        def xml = instance.encodeAsXML()
        request.xml = xml

        recordInputXML "updateInput", xml, controllerName

        controller.update()

        GPathResult updated = response.xml

        recordResult 'updateOk', updated, controllerName

        then:
        updated
        updated.@id == loadItem1.id
        updated.@version == loadItem1.version
        xmlPropertyCheck(updated, instance)

    }

    def "Do not create new instance with bad XML"() {
        expect:
        !RelationshipType.findByName("")

        when:
        response.format = "xml"
        def xml = badInstance.encodeAsXML()
        request.xml = xml

        recordInputXML "saveErrorsInput", xml, controllerName

        controller.save()

        GPathResult created = response.xml
        def stored = RelationshipType.findByName("")

        recordResult 'saveErrors', created, controllerName

        then:
        !stored
        created
        created == "Property [name] of class [class uk.co.mc.core.${resourceName.capitalize()}] cannot be null"
    }

    def "edit instance with bad JSON name"() {
        def instance = RelationshipType.findByName(loadItem1.name)

        expect:
        instance

        when:
        response.format = "json"
        params.id = instance.id
        request.json = [name: "g" * 256]

        controller.update()

        JSONObject updated = response.json

        recordResult 'updateErrors', updated, controllerName, relClass

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'


    }

    def "edit instance with bad XML"() {
        def instance = RelationshipType.findByName(loadItem1.name)

        expect:
        instance

        when:
        response.format = "xml"
        params.id = instance.id
        def xml = badInstance.encodeAsXML()

        recordInputXML "updateErrorsInput", xml, controllerName

        request.xml = xml

        controller.update()

        GPathResult updated = response.xml

        recordResult 'updateErrors', updated, controllerName

        then:
        updated
        updated == "Property [name] of class [class uk.co.mc.core.${resourceName.capitalize()}] cannot be null"


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
        !RelationshipType.get(params.id)
    }

    def "Return 204 for existing item as XML on delete"() {
        response.format = "xml"

        params.id = "1"

        controller.delete()

        expect:
        response.text == ""
        response.status == HttpServletResponse.SC_NO_CONTENT
        !RelationshipType.get(params.id)
    }


    boolean jsonPropertyCheck(json, loadItem) {
        for (int j = 0; (j < propertiesToCheck.size()); j++) {
            def property = propertiesToCheck[j]
            property = property.toString().replaceAll("\\@", "")
            def subProperties = property.split("\\.")
            def jsonProp = json
            def loadProp = loadItem

            if (subProperties.size() > 1) {
                for (int i = 0; (i < subProperties.size()); i++) {
                    def subProperty = subProperties[i]
                    jsonProp = jsonProp[subProperty]
                    loadProp = loadProp.getProperty(subProperty)
                }
            } else {
                jsonProp = json[property]
                loadProp = loadItem.getProperty(property)
            }

            def jsonMap = JSON.parse(jsonProp.toString())
            if (jsonMap) {
                jsonProp = JSON.parse(jsonProp.toString())
            } else {
                jsonProp = (jsonProp.toString()!="{}")?:null
                loadProp = (loadProp.toString()!="[:]")?:null
            }

            if (jsonProp != loadProp) {
                throw new AssertionError("error: property to check: ${propertiesToCheck[j]}  where json:${jsonProp} !=  item:${loadProp}")
            }
        }

        return true

    }


    boolean xmlPropertyCheck(xml, loadItem) {
        for (int j = 0; (j < propertiesToCheck.size()); j++) {
            def property = propertiesToCheck[j]
            def subProperties = property.toString().split("\\.")
            def xmlProp = xml
            def loadProp = loadItem

            if (subProperties.size() > 1) {
                for (int i = 0; (i < subProperties.size()); i++) {
                    def subProperty = subProperties[i]
                    if (subProperty.contains("@")) {
                        xmlProp = xmlProp[subProperty]
                        subProperty = subProperty.replaceAll("\\@", "")
                        loadProp = loadProp.getProperty(subProperty)
                    } else {
                        xmlProp = xmlProp[subProperty]
                        loadProp = loadProp.getProperty(subProperty)
                    }
                }
            } else {
                xmlProp = (xml[property].toString()) ?: null
                loadProp = loadItem.getProperty(property.toString().replaceAll("\\@", ""))
            }

            if (xmlProp.toString() != loadProp.toString()) {
                throw new AssertionError("error: property to check: ${propertiesToCheck[j]}  where xml:${xmlProp} !=  item:${loadProp}")
            }
        }
        return true

    }


    void fillWithDummyEntities(int limit = DUMMY_ENTITIES_COUNT) {
        if (limit <= RelationshipType.count()) return
        (RelationshipType.count() + 1).upto(limit) {
            assert RelationshipType.newInstance(getUniqueDummyConstructorArgs(it)).save()
        }
    }

    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "$resourceName $counter"]
    }



    protected RelationshipType prepareTypeAndDummyEntities() {
        fixturesLoader.load('relationshipTypes/RT_relationship')
        fillWithDummyEntities(15)
        RelationshipType relationshipType = fixturesLoader.RT_relationship.save() ?: RelationshipType.findByName('relationship')
        assert relationshipType
        relationshipType
    }


    protected static getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 5, 5, 0, 6, "${baseLink}?max=5&offset=10", ""],
                [2, 5, 5, 0, 6, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 3, 6, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 3, 6, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 5, 3, 6, "", "${baseLink}?max=5&offset=0"],
                [6, 2, 2, 3, 6, "", "${baseLink}?max=2&offset=8"]
        ]
    }


    // -- end copy and pasted

}


