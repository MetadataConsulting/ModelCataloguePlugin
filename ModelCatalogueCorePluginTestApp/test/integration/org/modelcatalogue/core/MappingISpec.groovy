package org.modelcatalogue.core

import spock.lang.Unroll

class MappingISpec extends AbstractIntegrationSpec {
    DataType dt1
    DataType dt2

    MappingService mappingService

    def setup() {
        initRelationshipTypes()
        dt1 = new DataType(name: 'test5').save(failOnError: true)
        dt2 = new DataType(name: 'test6').save(failOnError: true)
    }


    @Unroll
    def "create a new mapping from #args validates to #validates"() {
        int initialCount = Mapping.count()

        when:

        if (args.source) {
            args.source = new DataType(args.source).save(failOnError: true)
        }

        if (args.destination) {
            args.destination = new DataType(args.destination).save(failOnError: true)
        }


        Mapping mapping = new Mapping(args)
        mapping.save()


        then:

        (mapping.errors.errorCount == 0) == validates
        Mapping.count() == size + initialCount

        when:

        mapping.delete()

        then:

        true

        where:
        validates | size | args
        false     | 0    | [:]
        false     | 0    | [name: "x" * 256]
        false     | 0    | [ name: "String", source: [name: 'test5a'], destination: [name: 'test5b'], mapping: "foo" ]
        true      | 1    | [ name: "String1", source: [name: 'test5c'], destination: [name: 'test5d'], mapping: "x * 2" ]
    }

    def "map will fail if source and destination is the same"() {

        when:
        def dC = DataType.get(dt1.id)

        Mapping self = mappingService.map(dC, dC, "x")

        then:
        self
        self.hasErrors()
        self.errors.getFieldError("destination")
    }

    def "create mapping using map "() {
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
