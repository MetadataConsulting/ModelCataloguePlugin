package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class DataArchitectControllerIntegrationSpec {


    @Unroll
    def "#no -  search model catalogue - paginate results"(){

        def controller = new DataArchitectController()
        when:
        controller.response.format = "json"
        controller.index()
        JSONElement json = controller.response.json


        then:

        json.success
//        json.total == total
//        json.offset == offset
//        json.page == max
//        json.list
//        json.list.size() == size
//        json.next == next
//        json.previous == previous

    }


}
