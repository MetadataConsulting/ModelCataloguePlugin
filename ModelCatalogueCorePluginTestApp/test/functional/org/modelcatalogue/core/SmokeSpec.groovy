package org.modelcatalogue.core

import geb.spock.GebSpec

class SmokeSpec extends GebSpec {

    def "go to login"() {
        when:
        go ""

        then:
        title == "Model Catalogue Demo App"
    }

}