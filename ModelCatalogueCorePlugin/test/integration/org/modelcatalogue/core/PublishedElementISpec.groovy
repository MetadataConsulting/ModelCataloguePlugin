package org.modelcatalogue.core

import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class PublishedElementISpec extends AbstractIntegrationSpec{

    def setupSpec(){
        loadFixtures()
    }


    def "finalize model with with finalized child models"(){

        setup:
        PublishedElement el1 = new Model(name: "parent1").save()
        PublishedElement el2 = new Model(name: "child1").save()
        el1.addToParentOf(el2)

        when:
        el2.status = PublishedElementStatus.FINALIZED
        el2.save()
        el1.status = PublishedElementStatus.FINALIZED
        el1.save()

        then:
        el1.status == PublishedElementStatus.FINALIZED

        cleanup:
        el1.delete()
        el2.delete()

    }

    def "finalize model with with finalized data elements"(){

        setup:
        PublishedElement el1 = new Model(name: "parent1").save()
        PublishedElement el2 = new DataElement(name: "dataElement").save()
        el1.addToContains(el2)

        when:
        el2.status = PublishedElementStatus.FINALIZED
        el2.save()
        el1.status = PublishedElementStatus.FINALIZED
        el1.save()

        then:
        el1.status == PublishedElementStatus.FINALIZED

        cleanup:
        el1.delete()
        el2.delete()

    }



    def "finalize model without with finalized sub models"(){

        setup:
        PublishedElement el1 = new Model(name: "parent1").save()
        PublishedElement el2 = new Model(name: "child1").save()
        el1.addToParentOf(el2)

        when:
        el1.status = PublishedElementStatus.FINALIZED
        el1.save()
        el1.refresh()

        then:
        el1.hasErrors()

        cleanup:
        el1.delete()
        el2.delete()

    }

    def "finalize model without with finalized data elements"(){
        setup:
        PublishedElement el1 = new Model(name: "parent1").save()
        PublishedElement el2 = new DataElement(name: "dataElement").save()
        el1.addToContains(el2)

        when:
        el1.status = PublishedElementStatus.FINALIZED
        el1.save()

        then:
        el1.hasErrors()

        cleanup:
        el1.delete()
        el2.delete()
    }


}
