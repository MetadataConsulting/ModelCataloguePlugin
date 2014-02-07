package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Mapping)
class MappingSpec extends Specification{

    def "The Mapping gets persisted"(){

        expect:
        Mapping.list().isEmpty()

        when:
        Mapping type = new Mapping()

        type.save()

        then:
        type.id
        Mapping.list().size() == 1

        when:
        Mapping loaded = Mapping.get(type.id)

        then:
        loaded.sourceClass == DataType
        loaded.destinationClass == DataType
        loaded.sourceToDestination == "maps to"
        loaded.destinationToSource == "maps to"
        loaded.name == "mapping"

    }



}
