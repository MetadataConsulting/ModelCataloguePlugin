package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Hierarchy)
class HierarchySpec extends Specification{

    def "The context gets persisted"(){

        expect:
        Hierarchy.list().isEmpty()

        when:
        Hierarchy type = new Hierarchy()

        type.save()

        then:
        type.id
        Hierarchy.list().size() == 1

        when:
        Hierarchy loaded = Hierarchy.get(type.id)

        then:
        loaded.sourceClass == Model
        loaded.destinationClass == Model
        loaded.sourceToDestination == "parent of"
        loaded.destinationToSource == "child of"
        loaded.name == "hierarchy"

    }

    def "The hierarchy object cannot be edited"(){

        expect:
        Hierarchy.list().isEmpty()

        when:
        Hierarchy type = new Hierarchy()

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
