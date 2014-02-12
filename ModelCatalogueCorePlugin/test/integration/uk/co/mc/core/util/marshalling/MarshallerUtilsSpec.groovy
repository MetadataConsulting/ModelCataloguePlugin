package uk.co.mc.core.util.marshalling

import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType

/**
 * Created by adammilward on 10/02/2014.
 */
class MarshallerUtilsSpec extends Specification{

    def "test json marshalling for outgoing relationships"(){


        when:

        def dataElement1 = new DataElement(name: "TEstOne", description: "First data element") .save()
        def dataElement2 = new DataElement(name: "TestTwo", description: "Second data element").save()
        def dataElement3 = new DataElement(name: "TestThree", description: "Third data element").save()

        def rt = new RelationshipType(name:"NarrowerTerm",
                sourceToDestination: "NarrowerTerm",
                destinationToSource: "NarrowerTerm",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        then:

        !dataElement1.hasErrors()
        !dataElement2.hasErrors()
        !dataElement3.hasErrors()
        !rt.hasErrors()

        when:

        Relationship.link(dataElement1, dataElement2, rt)
        Relationship.link(dataElement1, dataElement3, rt)

        then:

        def marshalledOutput = MarshallerUtils.marshallOutgoingRelationships(dataElement1)

        marshalledOutput[1].sourcePath =="/DataElement/$dataElement2.id"
        marshalledOutput[1].destinationPath =="/DataElement/$dataElement1.id"
        marshalledOutput[1].destinationName =="$dataElement1.name"
        marshalledOutput[1].relationshipType.name =="NarrowerTerm"
        marshalledOutput[1].sourceName =="$dataElement2.name"
        marshalledOutput[0].sourcePath =="/DataElement/$dataElement3.id"
        marshalledOutput[0].destinationPath =="/DataElement/$dataElement1.id"
        marshalledOutput[0].destinationName =="$dataElement1.name"
        marshalledOutput[0].relationshipType.name =="NarrowerTerm"
        marshalledOutput[0].sourceName =="$dataElement3.name"


    }

    /*def cleanup(){
        Relationship.unlink(dataElement1, dataElement2, rt)
        Relationship.unlink(dataElement1, dataElement3, rt)
        dataElement1.delete()
        dataElement2.delete()
        dataElement3.delete()
        rt.delete()
    }*/

}
