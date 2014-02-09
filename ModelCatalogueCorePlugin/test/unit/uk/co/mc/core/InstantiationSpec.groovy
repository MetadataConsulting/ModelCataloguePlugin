package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Instantiation)
class InstantiationSpec extends Specification{

    def "The Include gets persisted"(){

        expect:
        Instantiation.list().isEmpty()

        when:
        Instantiation type = new Instantiation()

        type.save()

        then:
        type.id
        Instantiation.list().size() == 1

        when:
        Instantiation loaded = Instantiation.get(type.id)

        then:
        loaded.sourceClass == DataElement
        loaded.destinationClass == ValueDomain
        loaded.sourceToDestination == "instantiated by"
        loaded.destinationToSource == "instantiates"
        loaded.name == "instantiation"

    }

    def "The instantiation object cannot be edited"(){

        expect:
        Instantiation.list().isEmpty()

        when:
        Instantiation type = new Instantiation()

        type.save()

        then:

        !type.hasErrors()

        when:

        def errors = false

        try{
            type.name = "another name"
        }catch (ReadOnlyPropertyException error){
            errors = true
        }

        then:

        errors


    }



}
