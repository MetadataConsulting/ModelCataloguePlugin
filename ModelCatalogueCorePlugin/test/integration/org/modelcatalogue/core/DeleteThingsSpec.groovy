package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Shared
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

class DeleteThingsSpec extends IntegrationSpec{

 //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel
    @Shared
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

        def m

        expect:

        assert(m = new MeasurementUnit(name:"cm per hour", symbol: "cmph").save())
        assert(new PrimitiveType(name: "ground_speed", measurementUnit: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle").save())


        when:

        controller.response.format = "json"

        controller.params.id = m.id

        controller.delete()

        def json = controller.response.json

        recorder.recordResult 'deleteFailed', json

        then:

        json.errors
        json.errors.startsWith "Cannot delete cm per hour due to referential integrity constraint violation"
        controller.response.status == HttpServletResponse.SC_CONFLICT

    }

}
