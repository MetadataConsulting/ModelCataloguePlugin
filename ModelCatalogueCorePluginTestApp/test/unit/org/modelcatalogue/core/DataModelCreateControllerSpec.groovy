package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DataModelCreateController)
class DataModelCreateControllerSpec extends Specification {

    def "create model contains dataModelList"() {
        given:
        controller.dataModelCreateService = Mock(DataModelCreateService)

        when:
        Map model = controller.create()

        then:
        controller.modelAndView.model.containsKey('dataModelList')
    }

    def "create model contains dataModelPolicyList"() {
        given:
        controller.dataModelCreateService = Mock(DataModelCreateService)

        when:
        controller.create()

        then:
        controller.modelAndView.model.containsKey('dataModelPolicyList')
    }

    def "create calls collaborator DataModelCreateService twice"() {
        given:
        controller.dataModelCreateService = Mock(DataModelCreateService)

        when:
        controller.create()

        then:
        1 * controller.dataModelCreateService.findAllDataModelPolicyGormService()
        1 * controller.dataModelCreateService.findAllDataModelWhichMaybeImported()
    }

}
