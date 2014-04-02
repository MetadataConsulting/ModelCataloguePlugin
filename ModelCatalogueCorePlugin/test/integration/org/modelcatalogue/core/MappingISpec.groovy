package org.modelcatalogue.core

import spock.lang.Shared
import spock.lang.Unroll

class MappingISpec extends AbstractIntegrationSpec {

    @Shared
    def degreeC, degreeF

    @Shared MappingService mappingService = new MappingService()

    def setupSpec(){
        loadFixtures()
        degreeC = ValueDomain.findByName("value domain Celsius")
        degreeF = ValueDomain.findByName("value domain Fahrenheit")

    }

    /*
    def cleanupSpec(){

        degreeC.delete()
        degreeF.delete()

    }*/


    @Unroll
    def "create a mew data type from #args validates to #validates" (){

        expect:

        Mapping.list().isEmpty()

        when:

        Mapping type = new Mapping(args)
        type.save()


        then:

        !type.hasErrors() == validates
        Mapping.list().size() == size

        when:

        type.delete()

        then:

        true

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [ name: "String", source: degreeC, destination: degreeF, mapping: "foo" ]
        true      | 1    | [ name: "String1", source: degreeC, destination: degreeF, mapping: "x * 2" ]



    }

    def "map will fail if source and destination is the same"(){

        when:

        def dC = ValueDomain.get(degreeC.id)

        Mapping self = mappingService.map(dC, dC, "x")

        then:
        self
        self.hasErrors()
        self.errors.getFieldError("destination")

    }

    def "create mapping using map "(){

        when:

        def dC = ValueDomain.get(degreeC.id)
        def dF = ValueDomain.get(degreeF.id)

        Mapping zero = mappingService.map(dC, new ValueDomain(name: "none"), "x")

        then:
        !zero

        when:
        Mapping first = mappingService.map(dC, dF, "x * 9/5 + 32")

        then:
        first
        !first.hasErrors()
        dC.outgoingMappings
        dC.outgoingMappings.contains(first)
        dF.incomingMappings
        dF.incomingMappings.contains(first)

        when:
        Mapping second = mappingService.map(dC, dF, "x * 9/5 + 32")

        then:
        second
        !second.hasErrors()
        first.id == second.id
        dC.outgoingMappings
        dC.outgoingMappings.contains(first)
        dF.incomingMappings
        dF.incomingMappings.contains(first)

        when:
        Mapping third = mappingService.unmap(dC, dF)

        then:
        third
        third.id == first.id
        !dC.outgoingMappings
        !dC.outgoingMappings.contains(first)
        !dF.incomingMappings
        !dF.incomingMappings.contains(first)

        when:
        Mapping fourth = mappingService.unmap(dC, dF)

        then:
        !fourth
        !dC.outgoingMappings
        !dC.outgoingMappings.contains(first)
        !dF.incomingMappings
        !dF.incomingMappings.contains(first)
    }
}
