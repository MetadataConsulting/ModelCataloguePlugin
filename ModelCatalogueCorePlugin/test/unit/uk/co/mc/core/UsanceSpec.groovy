package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Usance)
class UsanceSpec extends Specification{

    def "The Usance gets persisted"(){

        expect:
        Usance.list().isEmpty()

        when:
        Usance type = new Usance()

        type.save()

        then:
        type.id
        Usance.list().size() == 1

        when:
        Usance loaded = Usance.get(type.id)

        then:
        loaded.sourceClass == ValueDomain
        loaded.destinationClass == DataType
        loaded.sourceToDestination == "uses"
        loaded.destinationToSource == "used by"
        loaded.name == "usance"

    }



}
