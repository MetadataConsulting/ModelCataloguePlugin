package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
@Mock(ConceptualDomain)
class ConceptualDomainSpec extends Specification {


    @Unroll
    def "ConceptualDomain creation for #args results in #validates"()
    {

        expect:

        ConceptualDomain.list().isEmpty()

        when:

        ConceptualDomain conceptInstance = new ConceptualDomain(args)

        conceptInstance.save()

        then:

        !conceptInstance.hasErrors() == validates

        where:

        validates  | args
        false | [name: "", description: "test concept description"]
        false      | [name:"t"*256, description: "test concept description"]
        false      | [name:"test concept", description: "t"*2001]
        true       | [name:"test concept", description: "test concept description"]

    }
}
