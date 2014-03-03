package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import spock.lang.Shared
import spock.lang.Unroll
import uk.co.mc.core.util.ResultRecorder

import javax.servlet.http.HttpServletResponse

/**
 * Created by adammilward on 05/02/2014.
 */
//

//
@Mixin(ResultRecorder)
class DeleteThingsSpec extends IntegrationSpec{

    def "placeholder test"(){
        expect:
        true
    }

 //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel



    MeasurementUnitController controller
    String controllerName

    def setup(){
        controller = new MeasurementUnitController()
        controllerName = "$controller.resourceName"
    }

    @Unroll
    def "json bad delete i.e. MU used in another resource, returns errors"(){

        def m, et, vd1
        expect:

        assert(m = new MeasurementUnit(name:"cm per hour", symbol: "cmph").save())
        assert(et = new EnumeratedType(name: "enum", enumerations:['1':'this', '2':'that', '3':'theOther']).save())
        assert(vd1 = new ValueDomain(name: "ground_speed", unitOfMeasure: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save())


        when:

        controller.response.format = "json"

        controller.params.id = m.id

        controller.delete()

        def json = controller.response.json

        recordResult 'deleteFailed', json, "measurementUnit"

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
        assert(vd1 = new ValueDomain(name: "ground_speed", unitOfMeasure: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle", dataType: et).save())


        when:

        controller.response.format = "xml"

        controller.params.id = m.id

        controller.delete()

        GPathResult xml = controller.response.xml

        recordResult 'deleteFailed', xml, "measurementUnit"

        then:

        xml == "Cannot delete cm per hour due to referential integrity constraint violation (/measurementUnit/delete/${m.id})"
        controller.response.status == HttpServletResponse.SC_CONFLICT

    }



}
