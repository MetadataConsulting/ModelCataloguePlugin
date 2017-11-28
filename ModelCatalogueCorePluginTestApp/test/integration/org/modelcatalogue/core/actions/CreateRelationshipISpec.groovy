package org.modelcatalogue.core.actions

import static org.modelcatalogue.core.actions.AbstractActionRunner.encodeEntity
import static org.modelcatalogue.core.actions.AbstractActionRunner.normalizeDescription
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import spock.lang.Shared

class CreateRelationshipISpec extends AbstractIntegrationSpec {

    def relationshipService

    @Autowired AutowireCapableBeanFactory autowireCapableBeanFactory
    @Shared
    DataClass one, two
    @Shared
    RelationshipType relation, contains
    @Shared
    CreateRelationship createAction

    def setup() {
        loadFixtures()
        createAction = new CreateRelationship()
        createAction.relationshipService = relationshipService
        createAction.autowireCapableBeanFactory = autowireCapableBeanFactory
        one = DataClass.findByName("book")
        two = DataClass.findByName("chapter1")
        relation = RelationshipType.relatedToType
        contains = RelationshipType.containmentType
    }

    def "uses default action natural name"() {

        expect:
        createAction.naturalName == "Create Relationship"
        createAction.description == normalizeDescription(CreateRelationship.description)

        when:
        createAction.initWith(source: encodeEntity(one), destination: encodeEntity(two), type: encodeEntity(relation))

        then:
        createAction.message == """
            Create new relationship '   <a href='#/catalogue/dataClass/${one.id}'> Data Class 'book'</a>  related to <a href='#/catalogue/dataClass/${two.id}'> Data Class 'chapter1'</a> with following parameters:

                        Source: book
            Destination: chapter1
            Type: relatedTo
        """.stripIndent().trim()
    }


    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = createAction.validate([:])

        expect:
        errorsForEmpty.containsKey 'source'
        errorsForEmpty.containsKey 'destination'
        errorsForEmpty.containsKey 'type'

        when:
        Map<String, String> errorsForNonExisting = createAction.validate(source: 'gorm://org.modelacatalogue.core.DataClass:1233456')

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
        sw.toString() == "<a href='#/catalogue/dataClass/${one.id}'>Data Class 'book'</a> now <a href='#/catalogue/relationshipType/${relation.id}'>related to</a> <a href='#/catalogue/dataClass/${two.id}'>Data Class 'chapter1'</a>"
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