package org.modelcatalogue.core

class DataElementISpec extends AbstractIntegrationSpec{

    def auth1, auth3, auth2

    def setup() {
        loadFixtures()
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

        auth2     in auth1.incomingRelations
        auth1     in auth2.outgoingRelations
        auth3     in auth2.incomingRelations
        auth2     in auth3.outgoingRelations

        when:

        auth2.removeLinkTo(auth1, RelationshipType.supersessionType)
        auth2.removeLinkFrom(auth3, RelationshipType.supersessionType)
        auth2.save(flush:true)

        then:

        !auth1.incomingRelations.contains(auth2)
        !auth2.outgoingRelations.contains(auth1)
        !auth2.incomingRelations.contains(auth3)
        !auth3.outgoingRelations.contains(auth2)


    }


}
