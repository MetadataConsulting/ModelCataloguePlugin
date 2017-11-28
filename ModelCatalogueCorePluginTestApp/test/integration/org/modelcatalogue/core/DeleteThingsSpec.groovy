package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import org.modelcatalogue.core.api.ElementStatus
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

    def setup() {
        controller = new MeasurementUnitController()
        controllerName = "$controller.resourceName"
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePluginTestApp/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                controllerName
        )
    }

    @Unroll
    def "json bad delete i.e. MU used in another resource, returns errors"() {
        def m, dm

        expect:
        assert(dm = new DataModel(name: "delete things spec", status: ElementStatus.DRAFT).save(failOnError: true))
        assert(m = new MeasurementUnit(dataModel: dm, name:"cm per hour", symbol: "cmph").save(failOnError: true))
        assert(new PrimitiveType(dataModel: dm, name: "ground_speed", measurementUnit: m, regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?", description: "the ground speed of the moving vehicle").save(failOnError: true))


        when:

        controller.response.format = "json"

        controller.params.id = m.id

        controller.delete()

        def json = controller.response.json

        recorder.recordResult 'deleteFailed', json

        then:
        controller.response.status == HttpServletResponse.SC_CONFLICT
        json.errors
        json.errors instanceof List
        json.errors.size() == 1
        json.errors[0].message?.startsWith "Cannot automatically delete"

    }

}
