package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.test.spock.IntegrationSpec
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class DataArchitectControllerIntegrationSpec extends AbstractIntegrationSpec{


    @Shared
    def relationshipService, de1, de2, de3, de4, de5, vd, md,md2


    def setupSpec(){
        //domainModellerService.modelDomains()
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("DE_author1")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        vd = ValueDomain.findByName("value domain Celsius")
        md = Model.findByName("book")
        md2 = Model.findByName("chapter1")
        de1.addToContainedIn(md)
        de3.addToContainedIn(md2)
        de2.addToInstantiatedBy(vd)
        relationshipService.link(de3, de2, RelationshipType.findByName("supersession"))
        md.addToParentOf(md2)

        de1.ext.put("Data item No.", "C1031")
        de2.ext.put("Optional_Local_Identifier", "C1031")
    }

    def cleanupSpec(){
        de1.removeFromContainedIn(md)
        de3.removeFromContainedIn(md2)
        de2.removeFromInstantiatedBy(vd)
        md.removeFromParentOf(md2)
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
        json.total == 11
        json.offset == 0
        json.page == 10
        json.list
        json.list.size() == 10
        json.next == "/dataArchitect/uninstantiatedDataElements?max=10&offset=10"
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
        xml.@total.text() == "11"
        xml.@offset.text() == "0"
        xml.@page.text() =="10"
        xml.element
        xml.element.size() == 10
        xml.next.text() == "/dataArchitect/uninstantiatedDataElements?max=10&offset=10"
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
        xml.@page.text() =="10"
        xml.element
        xml.element.size() == 10
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
        xml.@page.text() =="10"
        xml.element
        xml.element.size() == 1
        //xml.next.text() == "/dataArchitect/metadataKeyCheck?max=10&key=metadata&offset=10"
        xml.previous.text() == ""
    }


}
