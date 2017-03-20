package org.modelcatalogue.core

import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Unroll

class DataArchitectControllerIntegrationSpec extends AbstractIntegrationSpec {

    def relationshipService, de1, de2, de3, de4, de5, md, md2


    def setup() {
        //domainModellerService.modelDomains()
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("auth")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        md = new DataClass(name: "testModel").save()
        md2 = new DataClass(name: "testModel2").save()
        md.addToContains(de1)
        md2.addToContains(de3)
        md.addToParentOf(md2)

        de1.ext.put("Data item No.", "C10311")
        de2.ext.put("Optional_Local_Identifier", "C10311")
    }

    def "json get sub model elements"() {
        def controller = new DataArchitectController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                "dataArchitect"
        )

        when:
        controller.params.put("dataClassId", md.id)
        controller.response.format = "json"
        controller.getSubModelElements()
        JSONElement json = controller.response.json
        String list = "sub_model_elements"
        recorder.recordResult list, json

        then:
        json.success
        json.total == 2
        json.offset == 0
        json.page == 10
        json.list
        json.list.size() == 2
        json.next == ""
        json.previous == ""
    }

    @Unroll
    def "json -  get data elements without metadata key from the catalogue"() {

        def controller = new DataArchitectController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                "dataArchitect"
        )
        when:
        controller.response.format = "json"
        controller.params.put("key", "metadata")
        controller.metadataKeyCheck(10)
        JSONElement json = controller.response.json
        String list = "metadataKey_missing_key_metadata"
        recorder.recordResult list, json

        then:

        json.success
        //json.total == 11
        json.offset == 0
        json.page == 10
        json.list
        json.list.size() == 10
        //json.next == "/dataArchitect/metadataKeyCheck?max=10&key=metadata&offset=10"
        json.previous == ""

    }

    @Unroll
    def "json -  create dataElement relationships"() {

        def controller = new DataArchitectController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                "dataArchitect"
        )
        when:
        controller.response.format = "json"
        controller.params.put("keyOne", "Data item No.")
        controller.params.put("keyTwo", "Optional_Local_Identifier")
        controller.findRelationsByMetadataKeys(10)
        JSONElement json = controller.response.json
        String list = "dataElement_Relationships"
        recorder.recordResult list, json

        then:

        json.success
        json.total == 1
        json.offset == 0
        json.page == 10
        json.list
        json.list.size() == 1
        //json.next == "/dataArchitect/metadataKeyCheck?max=10&key=metadata&offset=10"
        json.previous == ""

    }

    def "find some elements and return just string for not found from elementsFromCSV"() {
        def controller = new DataArchitectController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                "dataArchitect"
        )


        GrailsMockMultipartFile mockFile = new GrailsMockMultipartFile('csv', 'headers.txt', 'text/plain', 'DE AUTHOR;speed of vauxhall;speed of opel;whatever'.bytes)

        when:
        controller.request.addFile mockFile
        controller.response.format = "json"
        controller.params.put("separator", ";")
        controller.elementsFromCSV()
        JSONElement json = controller.response.json
        recorder.recordResult "elementsFromCSV", json

        then:

        json.size() == 4
        !(json[0] instanceof String)
        !(json[1] instanceof String)
        !(json[2] instanceof String)
        json[3] instanceof String


    }

}
