package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(EnumeratedType)
class EnumeratedTypeSpec extends Specification {

    @Unroll
    def "enumAsString can be set with an enumerations map which may contain null values"() {
        when:
        EnumeratedType domain = new EnumeratedType([enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']])

        then:
        domain.validate(['enumAsString'])

        when:
        domain = new EnumeratedType([enumerations: ['m': 'male', 'f': 'female', 'u': null]])

        then:
        domain.validate(['enumAsString'])
    }

    void 'test enumerations must be a map or Enumerations'() {
        when: 'supplying a list instead of a map'
        EnumeratedType domain = new EnumeratedType([enumerations: ['male']])

        then:
        !domain.validate(['enumAsString'])
    }


    void 'test name can have a maximum of 255 characters'() {
        given:
        EnumeratedType domain = new EnumeratedType()

        when: 'for a string of 5_000_001 characters'
        String str = 'a' * 5_000_001
        domain.enumAsString = str

        then: 'name validation fails'
        !domain.validate(['enumAsString'])
        domain.errors['enumAsString'].code == 'maxSize.exceeded'

        when: 'for a string of 5_000_000 characters'
        str = 'a' * 5_000_000
        domain.enumAsString = str

        then: 'enumAsString validation passes'
        domain.validate(['enumAsString'])
    }

    void 'test enumAsString can be null'() {
        given:
        EnumeratedType domain = new EnumeratedType()

        when:
        domain.enumAsString = null

        then:
        domain.validate(['enumAsString'])
    }

    void 'test name cannot be null'() {
        given:
        EnumeratedType domain = new EnumeratedType()

        when:
        domain.name = null

        then:
        !domain.validate(['name'])
        domain.errors['name'].code == 'nullable'
    }
}
