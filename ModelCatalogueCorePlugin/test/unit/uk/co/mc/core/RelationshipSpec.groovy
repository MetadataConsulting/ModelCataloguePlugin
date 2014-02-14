package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification

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



    def "check  EqualsAndHashCode works"(){

        when:
        def rt = new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: CatalogueElement,destinationClass: CatalogueElement).save()
        def rt2 = new RelationshipType(name:'relationship2', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: CatalogueElement,destinationClass: CatalogueElement).save()

        def de1 = new DataElement(name:"test2DE")
        def de2 = new DataElement(name:"test1DE")

        def a = Relationship.link( de1, de2, rt)
        def b = Relationship.link( de1, de2, rt)
        def c = Relationship.link( de1, new DataElement(name:"test3DE"), rt)
        def d = Relationship.link( de1, de2, rt2)

        then:
        a.equals(b)
        b.equals(a)
        !a.equals(c)
        !a.equals(d)

    }

}
