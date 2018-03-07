package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

@TestFor(DataImportController)
class DataImportControllerSpec extends Specification {

    def "if modelName parameter is not present in the request use File.originalFilename as modelname"() {
        given:
        MultipartFile file = Stub(MultipartFile) {
            getOriginalFilename() >> 'model.xls'
        }
        when:
        String result = controller.modelName(new MockHttpServletRequest(), file)

        then:
        'model.xls' == result

        when:
        MockHttpServletRequest req = new MockHttpServletRequest()
        req.setParameter('modelName', 'Model Name')
        result = controller.modelName(req, file)

        then:
        'Model Name' == result

    }
}
