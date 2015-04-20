package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.FriendlyErrors
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
    Long totalCount

    protected boolean getRecord() {
        false
    }

    def setupSpec(){
        loadMarshallers()
        loadFixtures()
        if (record) {
            recorder = DefaultResultRecorder.create(
                    "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                    resourceName
            )
        } else {
            recorder = DummyRecorder.INSTANCE
        }

        totalCount = 12
    }


    @Unroll
    def "list json items test: #no where max: #max offset: #offset"() {

        expect:
        resourceCount == totalCount

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
        resourceCount == totalCount

        where:
        [no, size, max, offset, total, next, previous] << optimize(getPaginationParameters("/${resourceName}/"))
    }

    protected Long getResourceCount() {
        resource.count()
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
//        asset.status == ElementStatus.FINALIZED

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
        resourceCount == totalCount

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
        resourceCount == totalCount

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
        resourceCount == totalCount
    }

    @Deprecated final boolean getTestCreateXml() { true }


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
        def loadItemInstance = resource.get(loadItem.id)
        if(loadItemInstance instanceof CatalogueElement && loadItemInstance.status != ElementStatus.DRAFT) {
            loadItemInstance.status = ElementStatus.DRAFT
            FriendlyErrors.failFriendlySave(loadItemInstance)
        }

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
        !updated.errors
        customJsonPropertyCheck instance, updated, resource.get(updated.id)
        resourceCount == totalCount

    }

    def "edit instance with bad JSON name"() {
        if (controller.readOnly) return

        def instance = resource.findByName(loadItem.name)

        expect:
        instance

        when:
        if (instance instanceof CatalogueElement && instance.status != ElementStatus.DRAFT) {
            instance.status = ElementStatus.DRAFT
            FriendlyErrors.failFriendlySave(instance)
        }

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
        resourceCount == totalCount


    }

    def "Return 404 for non-existing item as JSON"() {

        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.show()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resourceCount == totalCount
    }

    def "Return 404 for non-existing item as JSON on delete"() {
        if (controller.readOnly) return

        controller.response.format = "json"
        controller.params.id = "1000000"
        controller.delete()

        expect:
        controller.response.text == ""
        controller.response.status == HttpServletResponse.SC_NOT_FOUND
        resourceCount == totalCount
    }


    T prepareInstanceForDelete() {
        T elementToDelete = resource.newInstance(newInstance)
        if (controller instanceof AbstractRestfulController) {
            controller.cleanRelations(elementToDelete)
            elementToDelete.save()
            controller.bindRelations(elementToDelete, false, newInstance)
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
        resourceCount == totalCount
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

    protected final optimize(params, boolean skipRest = record) {
        if (skipRest) {
            return params
        }
        return [params[0]]
    }

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

    @Deprecated final String getBadXmlError(){}

    @Deprecated
    def checkXmlCorrectListValues(GPathResult xml, total, size, offset, max, next, previous) {}

    @Deprecated
    final xmlCustomPropertyCheck(xml, item){}

    @Deprecated
    final xmlCustomPropertyCheck(inputItem, xml, outputItem){}


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
    File recordInputJSON(String fixtureName, Map json) {
        recorder.recordInputJSON(fixtureName, json)
    }

    @Override
    File recordInputJSON(String fixtureName, String json) {
        recorder.recordInputJSON(fixtureName, json)
    }

    boolean removeAllRelations(Object instance) { true }

}
