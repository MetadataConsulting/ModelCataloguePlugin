package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 03/02/2014.
 */
@Mock(OntologyType)
class OntologyTypeSpec extends Specification {

    def "The relationship type get persisted"(){

        expect:
        OntologyType.list().isEmpty()

        when:
        OntologyType type = new OntologyType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )

        type.save()

        then:
        type.id
        OntologyType.list().size() == 1

        when:
        OntologyType loaded = OntologyType.get(type.id)

        then:
        loaded.sourceClass == DataElement
        loaded.destinationClass == DataElement
        loaded.sourceToDestination == "Parent"
        loaded.destinationToSource == "Child"
        loaded.name == "Child Type"

    }

    def "test constrainst violations on name"(){

        when:

        OntologyType type = new OntologyType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: DataElement
        )
        type.validate()

        then:
        !type.hasErrors()

        when:
        OntologyType type2 = new OntologyType(
                sourceToDestination: "x" * 256,
                destinationToSource: "x" * 256,
                name: "x" * 256,
                sourceClass: String,
                destinationClass: String
        )
        type2.validate()

        then:
        type2.hasErrors()
        type2.errors.getFieldError("name")
        type2.errors.getFieldError("destinationToSource")
        type2.errors.getFieldError("sourceToDestination")
        type2.errors.getFieldError("sourceClass")
        type2.errors.getFieldError("destinationClass")


    }

    @Unroll
    def "test validate is #validates for uk.co.mc.core.Relationship source #source and destination #target" (){
        OntologyType type = new OntologyType(
                sourceToDestination: "Parent",
                destinationToSource: "Child",
                name: "Child Type",
                sourceClass: DataElement,
                destinationClass: ValueDomain
        )

        expect:
        type.validateSourceDestination(source, target) == validates


        where:
        validates << [
                false,
                false,
                true
        ]

        source << [
              new DataElement(),
              new DataElement(),
              new DataElement()
        ]

        target << [
                "HI",
                new DataElement(),
                new ValueDomain()
        ]

    }

}
