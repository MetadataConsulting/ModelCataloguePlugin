package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Inclusion)
class InclusionSpec extends Specification{

    def "The Include gets persisted"(){

        expect:
        Inclusion.list().isEmpty()

        when:
        Inclusion type = new Inclusion()

        type.save()

        then:
        type.id
        Inclusion.list().size() == 1

        when:
        Inclusion loaded = Inclusion.get(type.id)

        then:
        loaded.sourceClass == ConceptualDomain
        loaded.destinationClass == ValueDomain
        loaded.sourceToDestination == "includes"
        loaded.destinationToSource == "included in"
        loaded.name == "include"

    }



}
