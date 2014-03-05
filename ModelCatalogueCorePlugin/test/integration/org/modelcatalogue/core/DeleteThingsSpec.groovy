package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 05/02/2014.
 */
//

//
class DeleteThingsSpec extends IntegrationSpec{

 //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel



    MeasurementUnitController controller
    String controllerName
    ResultRecorder recorder

    def setup(){
        controller = new MeasurementUnitController()
        controllerName = "$controller.resourceName"
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                controllerName
        )
    }

    @Unroll
    def "json bad delete i.e. MU used in another resource, returns errors"(){

        def m, et, vd1
        expect:

        assert(m = new MeasurementUnit(name:"cm per hour", symbol: "cmph").save())
        assert(et = new EnumeratedType(name: "enum", enumerations:['1':'this', '2':'that', '3':'theOther']).save())
        assert(new ValueDomain(name: "ground_speed", unitOfMeasure: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save())


        when:

        controller.response.format = "json"

        controller.params.id = m.id

        controller.delete()

        def json = controller.response.json

        recorder.recordResult 'deleteFailed', json

        then:

        json.errors == "Cannot delete cm per hour due to referential integrity constraint violation (/measurementUnit/delete/${m.id})"
        controller.response.status == HttpServletResponse.SC_CONFLICT

    }

    @Unroll
    def "xml bad delete i.e. MU used in another resource, returns errors"(){

        def m, et, vd1
        expect:

        def controller = new MeasurementUnitController()

        assert(m = new MeasurementUnit(name:"cm per hour", symbol: "cmph").save())
        assert(et = new EnumeratedType(name: "enum", enumerations:['1':'this', '2':'that', '3':'theOther']).save())
        assert(new ValueDomain(name: "ground_speed", unitOfMeasure: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save())


        when:

        controller.response.format = "xml"

        controller.params.id = m.id

        controller.delete()

        GPathResult xml = controller.response.xml

        recorder.recordResult 'deleteFailed', xml

        then:

        xml == "Cannot delete cm per hour due to referential integrity constraint violation (/measurementUnit/delete/${m.id})"
        controller.response.status == HttpServletResponse.SC_CONFLICT

    }



}
