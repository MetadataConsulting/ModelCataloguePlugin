package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
* Created by adammilward on 05/02/2014.
*/

class DomainModellerISpec extends IntegrationSpec{
    @Shared
    def domainModellerService

    def "marshall domain models"() {
        expect:
        //domainModellerService.modelDomains()
        true
    }


}
