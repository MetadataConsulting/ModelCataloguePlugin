package org.modelcatalogue.core

import grails.converters.XML
import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ResultRecorder
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import spock.lang.Shared
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 27/02/2014.
 */
abstract class AbstractControllerIntegrationSpec<T> extends AbstractIntegrationSpec implements ResultRecorder{

    @Shared
    ResultRecorder recorder
    @Shared
    def totalCount

    def setupSpec(){
        loadMarshallers()
        loadFixtures()
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                resourceName
        )
        totalCount = 12
    }


    @Unroll
    def "list json items test: #no where max: #max offset: #offset"() {

        expect:
        resource.count() == totalCount

        when:
        controller.response.format = "json"
        controller.params.max = max
        controller.params.offset = offset
        controller.index()
        JSONElement json = controller.response.json
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
        json.itemType == resource.name
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/")
    }


    @Unroll
    def "list xml items test: #no where max: #max offset: #offset"() {

        expect:
        resource.count() == totalCount

        when:
        controller.response.format = "xml"
        controller.params.max = max
        controller.params.offset = offset
        controller.index()
        GPathResult xml = controller.response.xml
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
        resource.count() == totalCount

        where:
        [no, size, max, offset, total, next, previous] << getPaginationParameters("/${resourceName}/")
    }

    def "Export items to excel test"() {
        // TODO: fix threading issue
//        controller.params._x_wait_for_completion = true
        controller.response.format = "xlsx"
        controller.index()


        def link = controller.response.getHeader('Location')
        def id   = controller.response.getHeader('X-Asset-ID')

        expect:
        link
        id

        when:
        Asset asset = Asset.get(id as Long)

        then:
        asset.contentType == XLSXListRenderer.XLSX.name
//        asset.status == PublishedElementStatus.FINALIZED

//        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(controller.response.contentAsByteArray))
//
//        expect:
//        controller.response.contentType == XLSXListRenderer.EXCEL.name
//        workbook
//        workbook.getSheetAt(workbook.getActiveSheetIndex()).getLastRowNum() == totalRowsExported
//        // TODO: read the config and test the right number of columns as well
    }

    protected getTotalRowsExported() {
        totalCount
    }

    def "Show single existing item as JSON"() {

        when:
        controller.response.format = "json"
        controller.params.id = "${loadItem.id}"
        controller.show()
        JSONObject json = controller.response.json
        recordResult 'showOne', json

        then:
        json
        json.link == "/${GrailsNameUtils.getPropertyName(loadItem.class)}/${loadItem.id}"
        customJsonPropertyCheck loadItem, json
        resource.count() == totalCount

    }


    def "Show single existing item as XML"() {

        when:
        controller.response.format = "xml"
        controller.params.id = "${loadItem.id}"
        controller.show()
        println controller.response.text
        GPathResult xml = controller.response.xml
        recordResult "showOne", xml

        then:
        xml
        xml.@link
        xml.@link.text() == "/${GrailsNameUtils.getPropertyName(loadItem.class)}/${loadItem.id}"
        xmlCustomPropertyCheck xml, loadItem
        resource.count() == totalCount

    }


    @Unroll
    def "Do not #action new instance from JSON with bad json name"() {
        if (controller.readOnly) return

        expect:
        !resource.findByName(badInstance.name)

        when:
        controller.response.format = "json"
        def props = badInstance
        controller.request.json = props
        controller."$action"()
        JSONElement created = controller.response.json
        def stored = resource.findByName(badInstance.name)
        recordResult action + 'Errors', created

        then:
        !stored
        created
        created.errors
        created.errors.size() >= 1
        created.errors.find{it.field == "name"}
        resource.count() == totalCount

        where:
        action << ['save', 'validate']
    }

    def "Create new instance from JSON"() {
        if (controller.readOnly) return

        expect:
        !resource.findByName(newInstance.name)

        when:
        controller.response.format = "json"
        def json = newInstance
        controller.request.json = json
        recordInputJSON 'createInput', json
        controller.save()
        JSONObject created = controller.response.json
        def stored = resource.findByName(newInstance.name)
        recordResult 'saveOk', created

        then:
        stored
        created
        customJsonPropertyCheck newInstance, created, stored

        removeAllRelations stored

        stored.delete()
        resource.count() == totalCount
    }

    def "Create new instance from XML"() {
        if (controller.readOnly) return

        expect:
        !resource.findByName(newInstance.name)

        when:
        controller.response.format = "xml"
        controller.request.method = 'POST'
        def xml = resource.newInstance(newInstance)
        recordInputXML "createInput", xml.encodeAsXML()
        controller.request.setXML(xml as XML)
        controller.save()
        GPathResult created = controller.response.xml
        def stored = resource.findByName(newInstance.name)
        recordResult "createOk", created

        then:
        stored
        created
        created.@id == stored.id
        created.@version == stored.version
        xmlCustomPropertyCheck newInstance, created, stored
        stored.delete()
        resource.count() == totalCount

    }

    def "edit instance description from JSON"() {
        if (controller.readOnly) return

        def json = propertiesToEdit
        def properties = new HashMap()
        properties.putAll(loadItem.properties)
        def instance = resource.newInstance(properties)
        instance.properties = propertiesToEdit

        expect:
        instance
        loadItem
        json

        when:
        controller.response.format = "json"
        controller.params.id = loadItem.id
        controller.request.json = json
        controller.request.method = 'PUT'
        recordInputJSON 'updateInput', json
        controller.update()
        JSONObject updated = controller.response.json
        recordResult 'updateOk', updated

        then:
        updated
        customJsonPropertyCheck instance, updated, resource.get(updated.id)
        resource.count() == totalCount

    }

    def "edit instance from XML"() {
        if (controller.readOnly) return

        def properties = new HashMap()
        properties.putAll(loadItem.properties)
        def instance = resource.newInstance(properties)
        instance.properties = propertiesToEdit

        expect:
        instance
        loadItem

        when:
        controller.request.method = 'PUT'
        controller.response.format = "xml"
        controller.params.id = loadItem.id
        def xml = instance
        recordInputXML "updateInput", xml.encodeAsXML()
        controller.request.setXML(xml as XML)
        controller.update()
        GPathResult updated = controller.response.xml
        recordResult 'updateOk', updated

        then:
        updated
        xmlCustomPropertyCheck instance, updated, resource.get(updated.attributes().get("id"))
        resource.count() == totalCount

    }

    def "Do not create new instance with bad XML"() {
        if (controller.readOnly) return

        expect:
        !resource.findByName("")

        when:
        controller.request.method = 'PUT'
        controller.response.format = "xml"
        def xml = resource.newInstance(badInstance).encodeAsXML()
        controller.request.xml = xml
        recordInputXML action + "ErrorsInput", xml
        controller."$action"()
        GPathResult created = controller.response.xml
        def stored = resource.findByName("")
        recordResult action + 'Errors', created

        then:
        !stored
        created
        created.depthFirst().find { it.@field == 'name'} == getBadXmlError()
        resource.count() == totalCount

        where:
        action << ['save', 'validate']
    }

    def "edit instance with bad JSON name"() {
        if (controller.readOnly) return

        def instance = resource.findByName(loadItem.name)

        expect:
        instance

        when:
        controller.request.method = 'PUT'
        controller.response.format = "json"
        controller.params.id = instance.id
        controller.request.json = [name: "g" * 256]
        controller.update()
        JSONObject updated = controller.response.json
        recordResult 'updateErrors', updated

        then:
        updated
        updated.errors
        updated.errors.size() == 1
        updated.errors.first().field == 'name'
        resource.count() == totalCount


    }

    def "edit instance with bad XML"() {
        if (controller.readOnly) return

        def instance = resource.findByName(loadItem.name)

        expect:
        instance

        when:
        controller.request.method = 'PUT'
        controller.response.format = "xml"
        controller.params.id = instance.id
        def xml = resource.newInstance(badInstance).encodeAsXML()
        recordInputXML "updateErrorsInput", xml
        controller.request.xml = xml
        controller.update()
        GPathResult updated = controller.response.xml
        recordResult 'updateErrors', updated

        then:
        updated
        updated.depthFirst().find { it.@field == 'name'} == badXmlError
        resource.count() == totalCount

    }

    def "Return 404 for non-existing item as JSON"() {

        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.show()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML"() {
        controller.response.format = "xml"
        controller.params.id = "1000000"
        controller.show()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as JSON on delete"() {
        if (controller.readOnly) return

        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.delete()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }

    def "Return 404 for non-existing item as XML on delete"() {
        if (controller.readOnly) return

        controller.response.format = "xml"
        controller.params.id = "1000000"
        controller.params.id = "1000000"
        controller.delete()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resource.count() == totalCount
    }


    T prepareInstanceForDelete() {
        T elementToDelete = resource.newInstance(newInstance)
        if (controller instanceof AbstractRestfulController) {
            controller.cleanRelations(elementToDelete)
            elementToDelete.save()
            controller.bindRelations(elementToDelete, newInstance)
            return elementToDelete
        }
        elementToDelete.save()
    }

    def "Return 204 for existing item as JSON on delete"() {
        if (controller.readOnly) return

        def elementToDelete = prepareInstanceForDelete()
        removeAllRelations(elementToDelete)
        controller.response.format = "json"
        controller.params.id = elementToDelete.id
        controller.delete()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NO_CONTENT
        !resource.get(controller.params.id)
        resource.count() == totalCount
    }

    def "Return 204 for existing item as XML on delete"() {
        if (controller.readOnly) return

        def elementToDelete = prepareInstanceForDelete()
        removeAllRelations(elementToDelete)
        controller.response.format = "xml"
        controller.params.id = elementToDelete.id
        controller.delete()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NO_CONTENT
        !resource.get(controller.params.id)
        resource.count() == totalCount
    }

    abstract Map getPropertiesToEdit()
    //i.e. the properties map to pass in to test the edit functionality

    abstract Map getNewInstance()
    //i.e. a relationship type with good params to test create

    abstract Map getBadInstance()
    //i.e. a relationship type with bad params

    abstract Object getLoadItem()
    //i.e. a relationship type instance

    abstract Class<T> getResource()
    //i.e. RelationshipType

    abstract RestfulController<T> getController()
    //i.e. new RelationshipTypeController()

    abstract String getResourceName()
    //i.e. {GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")}

    abstract String getBadXmlError()

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, totalCount, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, totalCount, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, totalCount, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, totalCount, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 10, 10, totalCount, "", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, totalCount, "", "${baseLink}?max=2&offset=8"]
        ]
    }


    def checkXmlCorrectListValues(GPathResult xml, total, size, offset, max, next, previous) {
        assert xml.@success.text() == "true"
        assert xml.@total.text() == "${total}"
        assert xml.@size.text() == "${size}"
        assert xml.@offset.text() == "${offset}"
        assert xml.@page.text() == "${max}"
        assert xml.next.text() == next
        assert xml.previous.text() == previous
        true
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

        if (checkVersion) {
            checkProperty(json.version , outputItem.version, "version")
        }
        //jsonPropertyCheck json, outputItem
        return true
    }

    boolean isCheckVersion() { true }


    def checkProperty(property, value, String propertyName){
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

    def checkStringProperty(property, value, propertyName){
        if(property!= value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkMapProperty(Map property, Map value, propertyName) {
        if (property != value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkObjectMapStringProperty(JSONObject property, Map value, String propertyName){
        if (property != value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkPropertyMapMapString(Map property, Map value, String propertyName) {
        if (property!= value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }

    def checkStatusProperty(property, value, String propertyName){
        property = property.toString()
        value = value.toString()
        if(property!= value && (property!="" && value!=null)) {
            throw new AssertionError("error: property to check: ${propertyName}  where property: ${property} !=  item: ${value}")
        }
    }


//results recorder interface methods
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

    @Override
    File recordInputXML(String fixtureName, Map xml) {
        recorder.recordInputXML(fixtureName, xml)
    }

    @Override
    File recordInputXML(String fixtureName, XML xml) {
        recorder.recordInputXML(fixtureName, xml)
    }

    boolean removeAllRelations(Object instance) { true }

}
