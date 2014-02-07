package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Supersession)
class SupersessionSpec extends Specification{

    @Unroll
    def "The supersession relationship type get persisted"(){

        expect:
        Supersession.list().isEmpty()

        when:
        Supersession type = new Supersession()
        type.destinationClass = sourceClass
        type.sourceClass = DestinationClass
        type.validate()

        then:

        !type.hasErrors() == validates

        when:
        type.save()
        Supersession loaded = Supersession.get(type.id)

        then:
        Supersession.list().size() == size
        loaded.sourceClass == sourceClass
        loaded.destinationClass == DestinationClass
        loaded.sourceToDestination == "superseded by"
        loaded.destinationToSource == "supersedes"
        loaded.name == "supersession"

        where:

        validates | sourceClass | DestinationClass | size
        true      | DataElement | DataElement      | 1
        true      | Model       | Model            | 1

    }


    @Unroll
    def "The supersession relationship type is of the wrong type"(){

        expect:
        Supersession.list().isEmpty()

        when:
        Supersession type = new Supersession()
        type.destinationClass = sourceClass
        type.sourceClass = DestinationClass
        type.validate()

        then:

        !type.hasErrors() == validates


        where:

        validates | sourceClass | DestinationClass
        false     | ValueDomain | ValueDomain
        false     | DataElement | Model

    }


}
