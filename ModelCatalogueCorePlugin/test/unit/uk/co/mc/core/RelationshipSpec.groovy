package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import org.modelcatalogue.fixtures.FixturesLoader

/**
 * Created by adammilward on 03/02/2014.
 *
 * Relationship exist between catalogue elements of particular types (see relationship type)
 *
 *
 */

@Mock([Relationship, DataElement, DataType, RelationshipType])
class RelationshipSpec extends Specification{

    FixturesLoader fixturesLoader = new FixturesLoader("../ModelCatalogueCorePlugin/fixtures", getClass().getClassLoader())

    def "Won't create uk.co.mc.core.Relationship if the catalogue elements have not been persisted"()
    {

        expect:
        Relationship.list().isEmpty()

        when:

        Relationship rel =  Relationship.link(new DataElement(name:"test2DE") , new DataElement(name:"test1DE"), createRelationshipType())

        then:

        rel.hasErrors()


    }

    def "Create relationship then delete an element on one side of the relationship"(){

        def loadItem1, loadItem2, type, rel
        fixturesLoader.load('dataElements/DE_author', 'dataElements/DE_author1', 'dataElements/DE_author2', 'relationshipTypes/RT_relationship')

        assert (loadItem1 = fixturesLoader.DE_author.save())
        assert (loadItem2 = fixturesLoader.DE_author1.save())
        assert (type = fixturesLoader.RT_relationship.save())

        def item1Id = loadItem1.id
        def item2Id = loadItem2.id


        when:

        rel = Relationship.link(loadItem1, loadItem2, type)
       !rel.hasErrors()
        def relId = rel.id

        then:

        loadItem1.outgoingRelations == [loadItem2]
        loadItem2.incomingRelations == [loadItem1]


        when:

        loadItem1.delete(flush:true, failOnError:true)

        then:

        !DataElement.get(item1Id)
        !Relationship.get(relId)

        loadItem1.outgoingRelations == []
        loadItem2.incomingRelations == []


    }


    RelationshipType createRelationshipType(){
        new RelationshipType(name:'relationship1', sourceToDestination:'parent', destinationToSource: 'child', sourceClass: DataElement,destinationClass: DataElement)
    }

}
