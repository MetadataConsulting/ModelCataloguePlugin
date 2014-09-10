package org.modelcatalogue.core

import grails.converters.XML
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataImportController
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import spock.lang.Shared

/**
 * Created by sus_avi on 01/05/2014.
 */

class DataImportControllerSpec extends AbstractIntegrationSpec implements ResultRecorder{
    @Shared
    def fileName, recorder, filenameXsd, filenameXsd2
    def setupSpec(){
        fileName = "test/integration/resources/DataTemplate.xls"
        filenameXsd = "test/unit/resources/SACT/SACT_XMLDataTypes-v2-0.xsd"//"test/unit/resources/SACT/XSD_Example.xsd"
        filenameXsd2 = "test/unit/resources/SACT/SACTSACT_XMLSchema_EXAMPLE.xsd"//"test/unit/resources/SACT/XSD_Example.xsd"
        loadMarshallers()
        loadFixtures()
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                "Importer"
        )
    }

    //so we don't load a file
    def "placeholder"(){

    }

//uncomment locally

//    def "Test the dataImportService in the ImporterController"()
//    {
//        def controller = new DataImportController()
//        when: "The dataImportService is called"
//        def numElements = DataElement.count()
//        controller.response.format = 'json'
//        controller.metaClass.request = new MockMultipartHttpServletRequest()
//        controller.request.parameters = ['conceptualDomain' : 'test', 'name' : 'testImport123']
//        InputStream inputStream = new FileInputStream(fileName)
//        controller.request.addFile(new MockMultipartFile('file', fileName,"application/octet-stream" , inputStream))
//        controller.upload()
//        JSONElement json = controller.response.json
//        String list = "list1"
//        recordResult list, json
//
//        then: "The an importer is created and there are items in the importQueue and actions"
//        json
//        json.pendingAction
//        DataImport.list().size()>0
//
//    }

//    def "Test the dataImportService in the ImporterController"(){
//        def controller = new DataImportController()
//        when: "The dataImportService is called"
//        def numElements = DataElement.count()
//        controller.response.format = 'json'
//        controller.metaClass.request = new MockMultipartHttpServletRequest()
//        controller.params.conceptualDomain = 'test'
//        controller.params.name = 'testImport123'
//        InputStream inputStream = new FileInputStream(filenameXsd)
//        controller.request.addFile(new MockMultipartFile('file', filenameXsd,"application/octet-stream" , inputStream))
//        controller.upload()
//        JSONElement json = controller.response.json
//        String list = "list1"
//        recordResult list, json
//        def model = Model.findByName("qualifier")
//        def group = Model.findByName("group")
//        def cd = Model.findByName("CR")
//        def nhsDate = Model.findByName("TS.GB-en-NHS.Date")
//        def parents = model.parentOf
//        def children = model.childOf
//
//        then: "The an importer is created and there are items in the importQueue and actions"
//        ValueDomain.findByName("ts")
//        parents.contains(cd)
//        children.contains(group)
//        nhsDate.contains.collect{it.name=="value"}
//        json
//
//        when: "The dataImportService is called"
//        controller = new DataImportController()
//        numElements = DataElement.count()
//        controller.response.format = 'json'
//        controller.metaClass.request = new MockMultipartHttpServletRequest()
//        controller.params.conceptualDomain = 'test2'
//        controller.params.name = 'testImport1234'
//        InputStream inputStream2 = new FileInputStream(filenameXsd2)
//        controller.request.addFile(new MockMultipartFile('file', filenameXsd2,"application/octet-stream" , inputStream2))
//        controller.upload()
//        json = controller.response.json
////        def model = Model.findByName("qualifier")
////        def group = Model.findByName("group")
////        def cd = Model.findByName("CR")
////        def nhsDate = Model.findByName("TS.GB-en-NHS.Date")
////        def parents = model.parentOf
////        def children = model.childOf
//
//        then: "The an importer is created and there are items in the importQueue and actions"
////        ValueDomain.findByName("ts")
////        parents.contains(cd)
////        children.contains(group)
////        nhsDate.contains.collect{it.name=="value"}
//        json
//    }

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

}
