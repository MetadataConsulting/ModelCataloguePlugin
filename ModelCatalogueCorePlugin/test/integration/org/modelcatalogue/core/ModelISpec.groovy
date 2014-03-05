package org.modelcatalogue.core

import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class ModelISpec extends AbstractIntegrationSpec{

    @Shared
    def book

    def setupSpec(){
        loadFixtures()
        book = Model.findByName("book")
    }
/*
    def cleanupSpec(){
        book.delete()
    }
*/
    def "create a new model, finalize it and then try to change it"(){

        when:

        Model modelInstance = Model.get(book.id)
        modelInstance.save()

        then:

        !modelInstance.hasErrors()

        when:

        modelInstance.status = PublishedElementStatus.FINALIZED
        modelInstance.save(flush:true)

        then:

        !modelInstance.hasErrors()

        when:

        modelInstance.status = PublishedElementStatus.PENDING
        modelInstance.save()

        then:

        modelInstance.hasErrors()
        modelInstance.errors.getFieldError("status")?.code =='validator.finalized'

    }


}
