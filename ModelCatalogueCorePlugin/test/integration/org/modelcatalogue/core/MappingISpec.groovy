package org.modelcatalogue.core

import spock.lang.Unroll

class MappingISpec extends AbstractIntegrationSpec {


    DataType dt1
    DataType dt2

    MappingService mappingService

    def setup(){
        loadFixtures()
        dt1 = notNull DataType.findByName("test5")
        dt2 = notNull DataType.findByName("test6")
    }


    @Unroll
    def "create a new data type from #args validates to #validates" (){
        int initialCount = Mapping.count()

        when:

        Mapping type = new Mapping(args)
        type.save()


        then:

        (type.errors.errorCount == 0) == validates
        Mapping.count() == size + initialCount

        when:

        type.delete()

        then:

        true

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [ name: "String", source: notNull(DataType.findByName("test5")), destination: notNull(DataType.findByName("test6")), mapping: "foo" ]
        true      | 1    | [ name: "String1", source: notNull(DataType.findByName("test5")), destination: notNull(DataType.findByName("test6")), mapping: "x * 2" ]



    }

    def "map will fail if source and destination is the same"(){

        when:

        def dC = DataType.get(dt1.id)

        Mapping self = mappingService.map(dC, dC, "x")

        then:
        self
        self.hasErrors()
        self.errors.getFieldError("destination")

    }

    def "create mapping using map "(){

        when:

        def dC = DataType.get(dt1.id)
        def dF = DataType.get(dt2.id)

        Mapping zero = mappingService.map(dC, new DataType(name: "none"), "x")

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
