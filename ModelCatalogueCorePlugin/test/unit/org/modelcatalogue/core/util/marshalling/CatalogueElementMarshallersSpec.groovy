package org.modelcatalogue.core.util.marshalling

import grails.test.mixin.Mock
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeService
import spock.lang.Specification

@Mock(RelationshipType)
class CatalogueElementMarshallersSpec extends Specification {

    def "getting relationship configuration also from superclasses"() {
        CatalogueElementMarshallers marshallers = new CatalogueElementMarshallers(DataElement) {}
        marshallers.relationshipTypeService = new RelationshipTypeService()
        def relationships = marshallers.getRelationshipConfiguration(DataElement)

        expect:
        relationships.incoming
        relationships.incoming.supersession
        relationships.outgoing
        relationships.outgoing.supersession
    }
}
