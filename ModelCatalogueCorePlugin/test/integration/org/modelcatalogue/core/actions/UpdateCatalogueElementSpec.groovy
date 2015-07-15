package org.modelcatalogue.core.actions

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.RelationshipType

class UpdateCatalogueElementSpec extends IntegrationSpec {

    UpdateCatalogueElement updateAction = new UpdateCatalogueElement()
    DataClass model

    def setup() {
        model = new DataClass(name: 'The Model UA').save(failOnError: true)
        model.ext.bar = 'foo'
    }

    def "uses default action natural name"() {
        expect:
        updateAction.naturalName == "Update Catalogue Element"
        updateAction.description == AbstractActionRunner.normalizeDescription(UpdateCatalogueElement.description)

        when:
        updateAction.initWith(id: model.id.toString(), type: DataClass.name, name: 'The Updated Model')

        then:
        updateAction.message == """
            Update the Model 'The Model UA' with following parameters:

            Name: The Updated Model
        """.stripIndent().trim()
    }

    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = updateAction.validate([:])

        expect:
        errorsForEmpty.containsKey 'id'
        errorsForEmpty.containsKey 'type'

        when:
        Map<String, String> errorsForNonExistingClass = updateAction.validate(id: '100', type: 'org.example.Foo')

        then:
        errorsForNonExistingClass.containsKey 'type'

        when:
        Map<String, String> errorsForWrongType = updateAction.validate(id: '100', type: RelationshipType.name)

        then:
        errorsForWrongType.containsKey 'type'

        when:
        Map<String, String> errorsForWrongId = updateAction.validate(id: 'abc', type: DataClass.name)

        then:
        errorsForWrongId.containsKey 'id'

        when:
        Map<String, String> errorsForMissing = updateAction.validate(id: (model.id + 1).toString(), type: DataClass.name)

        then:
        errorsForMissing.containsKey 'id'

        when:
        Map<String, String> errorsForProperties = updateAction.validate(id: model.id.toString(), type: DataClass.name)

        then:
        errorsForProperties.containsKey 'properties'

        when:
        Map<String, String> errorsForCorrect = updateAction.validate(id: model.id.toString(), type: DataClass.name, name: "The Updated Model")

        then:
        DataClass.exists(model.id)
        errorsForCorrect.isEmpty()
    }

    def "the is action initialized with the parameters"() {
        expect:
        updateAction.id   == null
        updateAction.type == null


        when:
        updateAction.queryForCatalogueElement()

        then:
        thrown IllegalStateException

        when:
        updateAction.initWith(id: model.id.toString(), type: DataClass.name)
        def fetched = updateAction.queryForCatalogueElement()

        then:
        fetched == model
    }

    def "new instance is updated and saved to the database"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        updateAction.out = pw

        int initialCount = DataClass.count()

        when:
        updateAction.initWith(name: "The New Model Name", type: DataClass.name, id: model.id.toString(), 'ext:foo': 'bar', 'ext:bar': '')
        updateAction.run()

        DataClass changed = DataClass.findByName('The New Model Name')

        then:
        changed
        !updateAction.failed
        DataClass.count() == initialCount
        DataClass.countByName('The Model UA') == 0
        DataClass.countByName('The New Model Name') == 1
        sw.toString() == "<a href='#/catalogue/model/${changed.id}'>Model 'The New Model Name'</a> updated"
        changed.ext.foo == 'bar'
        changed.ext.bar == null
        updateAction.result == AbstractActionRunner.encodeEntity(DataClass.findByName('The New Model Name'))
    }

    def "error is reported to the output stream"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        updateAction.out = pw

        when:
        updateAction.initWith(name: "x" * 300, type: DataClass.name, id: model.id.toString())
        updateAction.run()

        then:
        updateAction.failed
        sw.toString().startsWith('Unable to update Model:' + model.id)
    }
}
