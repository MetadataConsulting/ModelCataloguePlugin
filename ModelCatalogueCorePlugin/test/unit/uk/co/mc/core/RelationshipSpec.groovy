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

@Mock([Relationship, OntologyRelationshipType, Context, Containment, Hierarchy, Inclusion, Instantiation, Mapping, Supersession, DataElement, ValueDomain, Model, MeasurementUnit, DataType])
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
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Context()]
        true       | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new Context()]
        false      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Containment()]
        true       | [source:new Model(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Containment()]
        false       | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: new Hierarchy()]
        true      | [source:new Model(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: new Hierarchy()]
        false       | [source:new DataElement(name: 'parentModel'),destination:new Model(name:'model2'),relationshipType: new Inclusion()]
        true       | [source:new ConceptualDomain(name: 'element1'),destination:new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name:"MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")), relationshipType: new Inclusion()]
        false       | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new Instantiation()]
        true       | [source:new DataElement(name: 'element1'),destination:new ValueDomain(name: "ground_speed", unitOfMeasure: new MeasurementUnit(name:"MPH"), regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?",  description: "the ground speed of the moving vehicle", dataType: new DataType(name: "Float")), relationshipType: new Instantiation()]
        false       | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Mapping()]
        true       | [source:new EnumeratedType(name: 'universitySubjects', enumerations: ['history', 'politics', 'science']), destination:new EnumeratedType(name: 'publicSubjects', enumerations: ['HIS', 'POL', 'SCI']),relationshipType: new Mapping(map: ['history':'HIS', 'politics':'POL', 'science':'SCI'])]
        false       | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new OntologyRelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for")]
        true      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new OntologyRelationshipType(name: "BroaderTerm", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower terms", sourceToDestination: "broader term for")]
        false       | [source:new ConceptualDomain(name: 'element1'),destination:new Model(name:'element2'),relationshipType: new Supersession()]
        true      | [source:new DataElement(name: 'element1'),destination:new DataElement(name:'element2'),relationshipType: new Supersession()]

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

    OntologyRelationshipType createRelationshipType(){
        new OntologyRelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: DataElement,destinationClass: DataElement)
    }
}
