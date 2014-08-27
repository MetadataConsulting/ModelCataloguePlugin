package org.modelcatalogue.core

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Created by adammilward on 27/02/2014.
 */

class DashboardControllerIntegrationSpec extends AbstractIntegrationSpec{

    def "new Test for json"(){

        def controller = new DashboardController()

        when:

        controller.request.method = "GET"
        controller.request.format = "json"
        controller.index()

        def response = controller.response.json

        then:
        response

    }

}
