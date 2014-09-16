package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class DataArchitectControllerIntegrationSpec extends AbstractIntegrationSpec {
@Shared
def relationshipService, de1, de2, de3, de4, de5, vd, md,md2


def setupSpec(){
    //domainModellerService.modelDomains()
    loadFixtures()
    de1 = DataElement.findByName("DE_author")
    de2 = DataElement.findByName("auth")
    de3 = DataElement.findByName("AUTHOR")
    de4 = DataElement.findByName("auth4")
    de5 = DataElement.findByName("auth5")
    vd = ValueDomain.findByName("value domain Celsius")
    md = new Model(name:"testModel").save()
    md2 = new Model(name:"testModel2").save()
    md.addToContains(de1)
    md2.addToContains(de3)
    md.addToParentOf(md2)

    de1.ext.put("Data item No.", "C10311")
    de2.ext.put("Optional_Local_Identifier", "C10311")
}

def cleanupSpec(){
    md.refresh()
    md.removeFromContains(de1)
    md2.refresh()
    md2.removeFromContains(de3)
    md.refresh()
    md.delete(flush:true)
    md2.refresh()
    md2.delete(flush:true)
}

def "json get sub model elements"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.params.put("modelId", md.id)
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

def "xml get sub model elements"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.params.put("modelId", md.id)
    controller.response.format = "xml"
    controller.getSubModelElements()
    GPathResult xml = controller.response.xml
    String list = "sub_model_elements"
    recorder.recordResult list, xml

    then:

    xml.@success.text() == "true"
    xml.@total.text() == "2"
    xml.@offset.text() == "0"
    xml.@page.text() =="10"
    xml.element
    xml.element.size() == 2
    xml.next.text() == ""
    xml.previous.text() == ""
}


@Unroll
def "json -  get uninstantiated data elements from the catalogue"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.response.format = "json"
    controller.uninstantiatedDataElements()
    JSONElement json = controller.response.json
    String list = "metadata_uninstantiated"
    recorder.recordResult list, json

    then:

    json.success
    json.total == 8
    json.offset == 0
    json.page == 10
    json.list
    json.list.size() == 8
    json.next == ""
    json.previous == ""


}

@Unroll
def "json -  get data elements without metadata key from the catalogue"(){

    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )
    when:
    controller.response.format = "json"
    controller.params.put("key", "metadata")
    controller.metadataKeyCheck()
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
def "xml -  get uninstantiated data elements from the catalogue"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.response.format = "xml"
    controller.uninstantiatedDataElements()
    GPathResult xml = controller.response.xml
    String list = "metadata_uninstantiated"
    recorder.recordResult list, xml

    then:

    xml.@success.text() == "true"
    xml.@offset.text() == "0"
    xml.@page.text() =="10000"
    xml.element
    xml.next.text() == ""
    xml.previous.text() == ""
}

@Unroll
def "xml -  get data elements without metadata key from the catalogue"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.response.format = "xml"
    controller.params.put("key", "metadata")
    controller.metadataKeyCheck()
    GPathResult xml = controller.response.xml
    String list = "metadataKey_missing_key_metadata"
    recorder.recordResult list, xml

    then:

    xml.@success.text() == "true"
    //xml.@total.text() == "11"
    xml.@offset.text() == "0"
    xml.@page.text() =="10000"
    xml.element
//        xml.element.size() == 11
    //xml.next.text() == "/dataArchitect/metadataKeyCheck?max=10&key=metadata&offset=10"
    xml.previous.text() == ""
}

@Unroll
def "json -  create dataElement relationships"(){

    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )
    when:
    controller.response.format = "json"
    controller.params.put("keyOne", "Data item No.")
    controller.params.put("keyTwo", "Optional_Local_Identifier")
    controller.findRelationsByMetadataKeys()
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
@Unroll
def "xml -  create dataElement relationships"(){
    def controller = new DataArchitectController()
    ResultRecorder recorder = DefaultResultRecorder.create(
            "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
            "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
            "dataArchitect"
    )

    when:
    controller.response.format = "xml"
    controller.params.put("keyOne", "Data item No.")
    controller.params.put("keyTwo", "Optional_Local_Identifier")
    controller.findRelationsByMetadataKeys()
    GPathResult xml = controller.response.xml
    String list = "dataElement_Relationships"
    recorder.recordResult list, xml

    then:

    xml.@success.text() == "true"
    xml.@total.text() == "1"
    xml.@offset.text() == "0"
    xml.@page.text() =="10000"
    xml.element
    xml.element.size() == 1
    //xml.next.text() == "/dataArchitect/metadataKeyCheck?max=10&key=metadata&offset=10"
    xml.previous.text() == ""
}

    def "find some elements and return just string for not found from elementsFromCSV"(){
        def controller = new DataArchitectController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
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
