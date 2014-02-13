package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class ModelISpec extends IntegrationSpec{

    @Shared
    def fixtureLoader, book

    def setupSpec(){
        def fixtures =  fixtureLoader.load("models/M_book")

        book = fixtures.M_book

    }
/*
    def cleanupSpec(){
        book.delete()
    }
*/
    def "create a new model, finalize it and then try to change it"(){

        expect:

        Model.list().size()==1
        when:

        Model modelInstance = Model.get(book.id)
        modelInstance.save()

        then:

        !modelInstance.hasErrors()

        when:

        modelInstance.status = PublishedElement.Status.FINALIZED
        modelInstance.save(flush:true)

        then:

        !modelInstance.hasErrors()

        when:

        modelInstance.status = PublishedElement.Status.PENDING
        modelInstance.save()

        then:

        modelInstance.hasErrors()
        modelInstance.errors.getFieldError("status")?.code =='validator.finalized'

    }


}
