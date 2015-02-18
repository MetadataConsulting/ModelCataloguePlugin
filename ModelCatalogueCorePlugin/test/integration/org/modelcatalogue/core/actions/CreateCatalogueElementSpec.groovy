package org.modelcatalogue.core.actions

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.RelationshipType

class CreateCatalogueElementSpec extends IntegrationSpec {

    CreateCatalogueElement createAction = new CreateCatalogueElement()

    def "uses default action natural name"() {
        expect:
        createAction.naturalName == "Create Catalogue Element"
        createAction.description == AbstractActionRunner.normalizeDescription(CreateCatalogueElement.description)

        when:
        createAction.initWith(name: 'The Model', type: Model.name)

        then:
        createAction.message == """
            Create new Model 'The Model' with following parameters:

            Name: The Model
        """.stripIndent().trim()
    }


    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = createAction.validate([:])

        expect:
        errorsForEmpty.containsKey 'name'
        errorsForEmpty.containsKey 'type'

        when:
        Map<String, String> errorsForNonExistingClass = createAction.validate(name: 'The Model', type: 'org.example.Foo')

        then:
        errorsForNonExistingClass.containsKey 'type'

        when:
        Map<String, String> errorsForWrongType = createAction.validate(name: 'The Model', type: RelationshipType.name)

        then:
        errorsForWrongType.containsKey 'type'

        when:
        Map<String, String> errorsForCorrect = createAction.validate(name: 'The Model', type: Model.name)

        then:
        errorsForCorrect.isEmpty()
    }

    def "the is action initialized with the parameters"() {
        expect:
        createAction.name == null
        createAction.type == null


        when:
        createAction.createCatalogueElement()

        then:
        thrown IllegalStateException

        when:
        createAction.initWith(name: "The Model", type: Model.name)
        def model = createAction.createCatalogueElement()

        then:
        model instanceof Model
    }

    def "new instance is created and saved to the database"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        createAction.out = pw

        int initialCount = Model.count()
        int draftCount   = Model.countByStatus(ElementStatus.DRAFT)

        when:
        createAction.initWith(name: "The Model", type: Model.name, description: "The Description", status: 'DRAFT', 'ext:foo': 'bar')
        createAction.run()

        Model created = Model.findByName('The Model')

        then:
        !createAction.failed
        created
        Model.count() == 1 + initialCount
        Model.countByName('The Model') == 1
        Model.countByStatus(ElementStatus.DRAFT) == 1 + draftCount
        sw.toString() == "New <a href='#/catalogue/model/${created.id}'>Model 'The Model'</a> created"
        created.ext.foo == 'bar'
        createAction.result == AbstractActionRunner.encodeEntity(Model.findByName('The Model'))
    }

    def "error is reported to the output stream"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        createAction.out = pw

        int initialCount = Model.count()

        when:
        createAction.initWith(name: "x" * 300, type: Model.name)
        createAction.run()

        then:
        createAction.failed
        Model.count() == initialCount
        sw.toString().startsWith('Unable to create new Model')
    }
}
