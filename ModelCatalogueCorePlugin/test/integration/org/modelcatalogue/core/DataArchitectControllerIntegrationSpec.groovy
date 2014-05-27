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
    def relationshipService, de1, de2, de3, de4, de5, vd, md


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
        de1.addToContainedIn(md)
        de2.addToInstantiatedBy(vd)
        relationshipService.link(de3, de2, RelationshipType.findByName("supersession"))
    }

    def cleanupSpec(){
        de1.removeFromContainedIn(md)
        de2.removeFromInstantiatedBy(vd)
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

}
