package uk.co.mc.core

import grails.test.mixin.Mock
import org.relaxng.datatype.Datatype
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 03/02/2014.
 *
 * Relationship exist between catalogue elements of particular types (see relationship type)
 *
 *
 */

@Mock([Relationship, DataElement, ValueDomain, Model, MeasurementUnit, DataType, RelationshipType])
class RelationshipSpec extends Specification{

    def "Won't create uk.co.mc.core.Relationship if the catalogue elements have not been persisted"()
    {

        expect:
        Relationship.list().isEmpty()

        when:

        Relationship rel =  Relationship.link(new DataElement(name:"test2DE") , new DataElement(name:"test1DE"), createRelationshipType())

        then:

        rel.hasErrors()


    }


    RelationshipType createRelationshipType(){
        new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: DataElement,destinationClass: DataElement)
    }
}
