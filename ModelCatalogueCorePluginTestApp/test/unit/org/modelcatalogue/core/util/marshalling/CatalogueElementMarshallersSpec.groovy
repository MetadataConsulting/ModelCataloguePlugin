package org.modelcatalogue.core.util.marshalling

import grails.test.mixin.Mock
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeService
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import spock.lang.Specification

@Mock(RelationshipType)
class CatalogueElementMarshallersSpec extends Specification {

    def "getting relationship configuration also from superclasses"() {
        CatalogueElementMarshaller marshallers = new CatalogueElementMarshaller(DataElement) {}
        marshallers.relationshipTypeService = new RelationshipTypeService()
        marshallers.relationshipTypeService.relationshipTypeGormService = Stub(RelationshipTypeGormService) {
            findRelationshipTypes() >> []
        }

        def relationships = marshallers.relationshipTypeService.getRelationshipConfiguration(DataElement)

        expect:
        relationships.incoming
        relationships.incoming.supersession
        relationships.outgoing
        relationships.outgoing.supersession
    }
}
