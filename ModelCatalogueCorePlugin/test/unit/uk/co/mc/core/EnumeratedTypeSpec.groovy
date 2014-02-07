package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Mock(EnumeratedType)
class EnumeratedTypeSpec extends Specification {


   @Unroll
   def "validates to #validates for #args "() {

        expect:

            EnumeratedType.list().isEmpty()

       when:

            EnumeratedType etype = new EnumeratedType(args)

            etype.save()

       then:

       !etype.hasErrors() == validates

       where:

       validates|args
        false   | [ : ]
        false   | [name:'test']
        false   | [name:'test', enumerations: ['male']]
        true    | [name:'test', enumerations: ['male','female','unknown'] ]



    }






}
