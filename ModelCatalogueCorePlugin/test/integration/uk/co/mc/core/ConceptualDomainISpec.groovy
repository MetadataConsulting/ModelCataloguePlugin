package uk.co.mc.core

import grails.test.spock.IntegrationSpec

/**
 * Created by adammilward on 12/02/2014.
 */
class ConceptualDomainISpec extends IntegrationSpec {

    def fixtureLoader

    def "test fixtures stuff"() {

        when:
        def fixtures =  fixtureLoader.load("conceptualDomains/universityLibraries",
                                            "conceptualDomains/publicLibraries")

        then:
        fixtures.universityLibraries.name == "university libraries"
        fixtures.publicLibraries.name == "public libraries"

    }
}
