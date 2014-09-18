package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeService
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import spock.lang.Specification

import static org.modelcatalogue.core.actions.AbstractActionRunner.encodeEntity
import static org.modelcatalogue.core.actions.AbstractActionRunner.normalizeDescription


@Mock([Model, ExtensionValue, RelationshipType, Relationship])
class CreateRelationshipSpec extends Specification {

    CreateRelationship createAction = new CreateRelationship()
    RelationshipTypeService relationshipTypeService = new RelationshipTypeService()

    Model one
    Model two
    RelationshipType relation
    RelationshipType contains


    def setup() {
        createAction.relationshipService = new RelationshipService()

        one = new Model(name: 'one').save(failOnError: true)
        two = new Model(name: 'two').save(failOnError: true)
        relation = new RelationshipType(name: 'relation', sourceClass: CatalogueElement, sourceToDestination: 'is related to', destinationClass: CatalogueElement, destinationToSource: 'is relation for').save(failOnError: true)
        contains = new RelationshipType(name: 'containment', sourceClass: CatalogueElement, sourceToDestination: 'contains', destinationClass: DataElement, destinationToSource: 'is contained in').save(failOnError: true)

        relation.relationshipTypeService = relationshipTypeService
        contains.relationshipTypeService = relationshipTypeService

        createAction.autowireCapableBeanFactory = Mock(AutowireCapableBeanFactory)
        createAction.autowireCapableBeanFactory.autowireBean(_) >> { it ->
            if (it instanceof RelationshipType) {
                it.relationshipTypeService = relationshipTypeService
            }

        }
    }

    def "uses default action natural name"() {
       expect:
        createAction.naturalName == "Create Relationship"
        createAction.description == normalizeDescription(CreateRelationship.description)

        when:
        createAction.initWith(source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(relation))

        then:
        createAction.message == """
            Create new relationship 'one is related to two' with following parameters:

            Source: one
            Destination: two
            Type: relation
        """.stripIndent().trim()
    }


    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = createAction.validate([:])

        expect:
        errorsForEmpty.containsKey 'source'
        errorsForEmpty.containsKey 'destination'
        errorsForEmpty.containsKey 'type'

        when:
        Map<String, String> errorsForNonExisting = createAction.validate(source: 'gorm://org.modelacatalogue.core.Model:1233456')

        then:
        errorsForNonExisting.containsKey 'source'

        when:
        Map<String, String> errorsForWrongRelation = createAction.validate(source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(contains))

        then:
        !errorsForWrongRelation.containsKey('type')
        errorsForWrongRelation.containsKey('destination')

        when:
        Map<String, String> errorsForCorrect = createAction.validate(source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(relation))

        then:
        errorsForCorrect.isEmpty()
    }

    def "the is action initialized with the parameters"() {
        when:
        createAction.link()

        then:
        thrown IllegalStateException

        when:
        createAction.initWith source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(relation)
        def relationship = createAction.link()

        then:
        relationship instanceof Relationship
    }

    def "new instance is created and saved to the database"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        createAction.out = pw

        expect:
        Relationship.count() == 0

        when:
        createAction.initWith source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(relation)
        createAction.run()

        then:
        !createAction.failed
        sw.toString() == "<a href='#/catalogue/model/${one.id}'>Model 'one'</a> now <a href='#/catalogue/relationshipType/${relation.id}'>is related to</a> <a href='#/catalogue/model/${two.id}'>Model 'two'</a>"
        createAction.result == encodeEntity(Relationship.list(limit: 1)[0])
    }

    def "error is reported to the output stream"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        createAction.out = pw

        expect:
        Relationship.count() == 0

        when:
        createAction.initWith(source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(contains))
        createAction.run()

        then:
        createAction.failed
        Relationship.count() == 0
        sw.toString().startsWith('Unable to create new relationship')
    }
}
