package org.modelcatalogue.core

import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends AbstractIntegrationSpec{

    @Shared
    def auth1, auth3, auth2

    def setupSpec(){
        loadFixtures()
    }

    def cleanupSpec(){
    }

    def "create a new data element, finalize it and then try to change it"(){

        when:
        def dataElementInstance = new DataElement(name: "result1", description: "this is the the result description")
        dataElementInstance.save()

        then:

        !dataElementInstance.hasErrors()

        when:
        dataElementInstance.status = PublishedElementStatus.FINALIZED
        dataElementInstance.save(flush:true)

        then:
        !dataElementInstance.hasErrors()

        when:
        dataElementInstance.status = PublishedElementStatus.PENDING
        dataElementInstance.save()

        then:
        dataElementInstance.hasErrors()
        dataElementInstance.errors.getFieldError("status")?.code =='validator.finalized'
        dataElementInstance.delete()

    }

    def "create writer data elements with the same code dataElement"(){

        when:
        def dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description", code: "XXX_1")
        dataElementInstance2.validate()

        then:

        dataElementInstance2.hasErrors()

    }


    def "get all relations"() {

        initCatalogueService.initDefaultRelationshipTypes()

        auth1 = DataElement.findByName("DE_author1")
        auth2 = DataElement.findByName("AUTHOR")
        auth3 = DataElement.findByName("auth")

        !auth1.hasErrors()
        !auth2.hasErrors()
        !auth3.hasErrors()

        when:

        auth2.createLinkTo(auth1, RelationshipType.supersessionType)
        auth2.createLinkFrom(auth3, RelationshipType.supersessionType)

        then:

        auth2     in auth1.relations
        auth1     in auth2.relations
        auth3      in auth2.relations
        auth2     in auth3.relations

        when:

        auth2.removeLinkTo(auth1, RelationshipType.supersessionType)
        auth2.removeLinkFrom(auth3, RelationshipType.supersessionType)
        auth2.save(flush:true)

        then:

        !auth1.relations.contains(auth2)
        !auth2.relations.contains(auth1)
        !auth2.relations.contains(auth3)
        !auth3.relations.contains(auth2)


    }





}
