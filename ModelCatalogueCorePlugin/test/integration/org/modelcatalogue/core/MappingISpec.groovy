package org.modelcatalogue.core

import spock.lang.Shared
import spock.lang.Unroll

class MappingISpec extends AbstractIntegrationSpec {

    @Shared
    def vd1, vd2

    @Shared MappingService mappingService = new MappingService()

    def setupSpec(){
        loadFixtures()
        vd1 = ValueDomain.findByName("value domain test3")
        vd2 = ValueDomain.findByName("value domain test4")
    }

    /*
    def cleanupSpec(){

        vd1.delete()
        vd2.delete()

    }*/


    @Unroll
    def "create a mew data type from #args validates to #validates" (){
        int initialCount = Mapping.count()

        when:

        Mapping type = new Mapping(args)
        type.save()


        then:

        !type.hasErrors() == validates
        Mapping.count()   == size + initialCount

        when:

        type.delete()

        then:

        true

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [ name: "String", source: vd1, destination: vd2, mapping: "foo" ]
        true      | 1    | [ name: "String1", source: vd1, destination: vd2, mapping: "x * 2" ]



    }

    def "map will fail if source and destination is the same"(){

        when:

        def dC = ValueDomain.get(vd1.id)

        Mapping self = mappingService.map(dC, dC, "x")

        then:
        self
        self.hasErrors()
        self.errors.getFieldError("destination")

    }

    def "create mapping using map "(){

        when:

        def dC = ValueDomain.get(vd1.id)
        def dF = ValueDomain.get(vd2.id)

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
