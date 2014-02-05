package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 03/02/2014.
 */

@Mock([Relationship, RelationshipType, DataElement])
class RelationshipSpec extends Specification{

    @Unroll
    def "uk.co.mc.core.Relationship creation for #args results #validates"()
    {

        expect:
        Relationship.list().isEmpty()

        when:
        Relationship rel=new Relationship(args);
        rel.save()

        then:
        !rel.hasErrors() == validates

        where:

        validates  | args
        false      | [ : ]
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2')]
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: createRelationshipType(ValueDomain)]
        true       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: createRelationshipType()]

    }

    def "Won't create uk.co.mc.core.Relationship if the catalogue elements have not been persisted"()
    {

        expect:
        Relationship.list().isEmpty()

        when:

        Relationship rel =  Relationship.link(new DataElement(name:"test2DE") , new DataElement(name:"test1DE"), createRelationshipType())

        then:

        rel.hasErrors()


    }

    RelationshipType createRelationshipType(Class destinationClass=DataElement){
        new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: DataElement,destinationClass: destinationClass )
    }
}
