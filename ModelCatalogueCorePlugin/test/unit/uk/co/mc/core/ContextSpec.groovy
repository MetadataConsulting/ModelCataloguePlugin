package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * Created by adammilward on 07/02/2014.
 */
@Mock(Context)
class ContextSpec extends Specification{

    def "The context gets persisted"(){

        expect:
        Context.list().isEmpty()

        when:
        Context type = new Context()

        type.save()

        then:
        type.id
        Context.list().size() == 1

        when:
        Context loaded = Context.get(type.id)

        then:
        loaded.sourceClass == ConceptualDomain
        loaded.destinationClass == Model
        loaded.sourceToDestination == "provides context for"
        loaded.destinationToSource == "has context of"
        loaded.name == "context"

    }

    def "The context object cannot be edited"(){

        expect:
        Context.list().isEmpty()

        when:
        Context type = new Context()

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
