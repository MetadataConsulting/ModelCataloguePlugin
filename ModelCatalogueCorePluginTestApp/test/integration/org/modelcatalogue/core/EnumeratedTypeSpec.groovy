package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class EnumeratedTypeSpec extends IntegrationSpec {


    @Unroll
    def "validates to #validates for #args.name "() {
        when:
        EnumeratedType etype = new EnumeratedType(args)

        etype.save()

        then:

        !etype.hasErrors() == validates

        where:

        validates | args
        false     | [:]
        true      | [name: 'test1']
        false     | [name: 'test2', enumerations: ['male']]
        false     | [name: 'test3', enumAsString: ('m:s|' * 50000) + 's:m']
        true      | [name: 'test4', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']]
        true      | [name: 'test5', enumerations: ['m': 'male', 'f': 'female', 'u': null]]
    }
}
