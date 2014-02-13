package uk.co.mc.core

import grails.test.spock.IntegrationSpec

/**
 * Created by adammilward on 12/02/2014.
 */
class ConceptualDomainISpec extends IntegrationSpec {

    def fixtureLoader

    def "test fixtures stuff"() {

        when:
        def fixtures =  fixtureLoader.load("conceptualDomains/CD_universityLibraries",
                                            "conceptualDomains/CD_publicLibraries")

        then:
        fixtures.CD_universityLibraries.name == "university libraries"
        fixtures.CD_publicLibraries.name == "public libraries"

    }
}
