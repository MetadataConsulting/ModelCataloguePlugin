package org.modelcatalogue.core

import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder

/**
 * Created by adammilward on 27/02/2014.
 */

class DashboardControllerIntegrationSpec extends AbstractIntegrationSpec{

    ResultRecorder recorder

    def "new Test for json"(){
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                'dashboard'
        )

        def controller = new DashboardController()

        when:

        controller.request.method = "GET"
        controller.request.format = "json"
        controller.index()

        def response = controller.response.json
        recorder.recordResult('index' , controller.response.json)

        then:
        response

    }

}
