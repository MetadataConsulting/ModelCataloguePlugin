package uk.co.mc.core.util.marshalling

import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.OntologyRelationshipType
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType
import uk.co.mc.core.util.marshalling.MarshallerUtils

/**
 * Created by adammilward on 10/02/2014.
 */
class MarshallerUtilsSpec extends Specification{


    def cleanup(){
        Relationship.list().each{ relationship ->
            Relationship.unlink(relationship.source, relationship.destination, relationship.relationshipType)
        }

        DataElement.list().each{ dataElement ->
            dataElement.delete()
        }

        RelationshipType.list().each{ relationshipType ->
            relationshipType.delete()
        }
    }

    def "test json marshalling for outgoing relationships"(){

        expect:

        DataElement.list().isEmpty()

        when:

        def de1 = new DataElement(id: 1, name: "One", description: "First data element", definition: "First data element definition").save()
        def de2 = new DataElement(id: 2, name: "Two", description: "Second data element", definition: "Second data element definition").save()
        def de3 = new DataElement(id: 3, name: "Three", description: "Third data element", definition: "Third data element definition").save()

        def rt = new OntologyRelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        def rel = Relationship.link(de1, de2, rt)
        def rel2 = Relationship.link(de1, de3, rt)

        then:

        def marshalledOutput = MarshallerUtils.marshallOutgoingRelationships(de1)

        marshalledOutput[1].sourcePath =="/DataElement/5"
        marshalledOutput[1].destinationPath =="/DataElement/4"
        marshalledOutput[1].destinationName =="One"
        marshalledOutput[1].relationshipType.sourceClass =="uk.co.mc.core.OntologyRelationshipType"
        marshalledOutput[1].sourceName =="Two"
        marshalledOutput[0].sourcePath =="/DataElement/6"
        marshalledOutput[0].destinationPath =="/DataElement/4"
        marshalledOutput[0].destinationName =="One"
        marshalledOutput[0].relationshipType.sourceClass =="uk.co.mc.core.OntologyRelationshipType"
        marshalledOutput[0].sourceName =="Three"


    }

}
