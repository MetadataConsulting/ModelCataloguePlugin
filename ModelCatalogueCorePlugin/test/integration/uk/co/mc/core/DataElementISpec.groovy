package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends Specification{

    def "create a new data element, finalize it and then try to change it"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance = new DataElement(name: "result1", description: "this is the the result description")
        dataElementInstance.save()

        then:

        !dataElementInstance.hasErrors()

        when:

        dataElementInstance.status = PublishedElement.Status.FINALIZED
        dataElementInstance.save(flush:true)

        then:

        !dataElementInstance.hasErrors()

        when:

        dataElementInstance.status = PublishedElement.Status.PENDING
        dataElementInstance.save()

        then:

        dataElementInstance.hasErrors()
        dataElementInstance.errors.getFieldError("status")?.code =='validator.finalized'

    }

    def "create two data elements with the same code dataElement"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance1 = new DataElement(name: "result1", description: "this is the the result description", "code": "x123")
        dataElementInstance1.save()

        DataElement dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description", "code": "x123")
        dataElementInstance2.save()

        then:

        dataElementInstance2.hasErrors()
        dataElementInstance2.errors.getFieldError("code") =='blah'

    }


}
