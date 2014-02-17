package uk.co.mc.core.fixtures

import spock.lang.Specification

class MockFixturesLoaderSpec extends Specification {

    def "mock fixtures loader loads fixture"() {
        MockFixturesLoader fixturesLoader = new MockFixturesLoader()

        when:
        fixturesLoader.load("dataTypes/DT_double")
        fixturesLoader.load("dataTypes/DT_string")

        then:
        fixturesLoader.DT_double
        fixturesLoader.DT_double.name == 'double'
        fixturesLoader.DT_string
        fixturesLoader.DT_string.name == 'string'
    }

}
