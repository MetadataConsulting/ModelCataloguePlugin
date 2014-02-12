package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by adammilward on 05/02/2014.
 */

class ModelISpec extends Specification{

    /*def cleanupSpec(){
        Model.list().each{ model ->

            model.delete()

        }
    }*/

    def "create a new model, finalize it and then try to change it"(){

        expect:

        Model.list().isEmpty()

        when:

        Model modelInstance = new Model(name: "result1", description: "this is the the result description")
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
