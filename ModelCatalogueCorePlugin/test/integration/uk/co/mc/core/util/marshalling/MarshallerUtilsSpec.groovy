package uk.co.mc.core.util.marshalling

import spock.lang.Specification
import uk.co.mc.core.DataElement
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


        when:

        def de1 = new DataElement(id: 1, name: "One", description: "First data element", definition: "First data element definition").save()
        def de2 = new DataElement(id: 2, name: "Two", description: "Second data element", definition: "Second data element definition").save()
        def de3 = new DataElement(id: 3, name: "Three", description: "Third data element", definition: "Third data element definition").save()

        def rt = new RelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        def rel = Relationship.link(de1, de2, rt)
        def rel2 = Relationship.link(de1, de3, rt)

        then:

        def marshalledOutput = MarshallerUtils.marshallOutgoingRelationships(de1)

        marshalledOutput[1].sourcePath =="/DataElement/$de2.id"
        marshalledOutput[1].destinationPath =="/DataElement/$de1.id"
        marshalledOutput[1].destinationName =="$de1.name"
        marshalledOutput[1].relationshipType.name =="Synonym"
        marshalledOutput[1].sourceName =="$de2.name"
        marshalledOutput[0].sourcePath =="/DataElement/$de3.id"
        marshalledOutput[0].destinationPath =="/DataElement/$de1.id"
        marshalledOutput[0].destinationName =="$de1.name"
        marshalledOutput[0].relationshipType.name =="Synonym"
        marshalledOutput[0].sourceName =="$de3.name"


    }

}
