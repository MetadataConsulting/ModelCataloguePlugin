package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.DataElement
import spock.lang.Specification

class CatalogueElementMarshallersSpec extends Specification {

    def "getting relationship configuration also from superclasses"() {
        def relationships = CatalogueElementMarshallers.getRelationshipConfiguration(DataElement)

        expect:
        relationships.incoming
        relationships.incoming.supersession
        relationships.outgoing
        relationships.outgoing.supersession
    }
}
