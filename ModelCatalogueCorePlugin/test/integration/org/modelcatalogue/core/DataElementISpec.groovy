package org.modelcatalogue.core
/**
 * Created by adammilward on 05/02/2014.
 */

class DataElementISpec extends AbstractIntegrationSpec{

    def auth1, auth3, auth2

    def setup(){
        loadFixtures()
    }

    def "create writer data elements with the same code dataElement"(){

        when:
        def dataElementInstance2 = new DataElement(name: "result2", description: "this is the the result2 description")
        dataElementInstance2.modelCatalogueId = "XXX_1"
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
