package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Containment)
class ContainmentSpec extends Specification{

    def "The Containment gets persisted"(){

        expect:
        Containment.list().isEmpty()

        when:
        Containment type = new Containment()

        type.save()

        then:
        type.id
        Containment.list().size() == 1

        when:
        Containment loaded = Containment.get(type.id)

        then:
        loaded.sourceClass == Model
        loaded.destinationClass == DataElement
        loaded.sourceToDestination == "contains"
        loaded.destinationToSource == "contained in"
        loaded.name == "containment"

    }



}
