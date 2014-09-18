package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.RelationshipType
import spock.lang.Specification


@Mock([Model, ExtensionValue])
class UpdateCatalogueElementSpec extends Specification {

    UpdateCatalogueElement updateAction = new UpdateCatalogueElement()
    Model model

    def setup() {
        model = new Model(name: 'The Model').save(failOnError: true)
        model.ext.bar = 'foo'
    }

    def "uses default action natural name"() {
        expect:
        updateAction.naturalName == "Update Catalogue Element"
        updateAction.description == AbstractActionRunner.normalizeDescription(UpdateCatalogueElement.description)

        when:
        updateAction.initWith(id: model.id.toString(), type: Model.name, name: 'The Updated Model')

        then:
        updateAction.message == """
            Update the Model 'The Model' with following parameters:

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
        Map<String, String> errorsForWrongId = updateAction.validate(id: 'abc', type: Model.name)

        then:
        errorsForWrongId.containsKey 'id'

        when:
        Map<String, String> errorsForMissing = updateAction.validate(id: (model.id + 1).toString(), type: Model.name)

        then:
        errorsForMissing.containsKey 'id'

        when:
        Map<String, String> errorsForProperties = updateAction.validate(id: model.id.toString(), type: Model.name)

        then:
        errorsForProperties.containsKey 'properties'

        when:
        Map<String, String> errorsForCorrect = updateAction.validate(id: model.id.toString(), type: Model.name, name: "The Updated Model")

        then:
        Model.exists(model.id)
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
        updateAction.initWith(id: model.id.toString(), type: Model.name)
        def fetched = updateAction.queryForCatalogueElement()

        then:
        fetched == model
    }

    def "new instance is updated and saved to the database"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        updateAction.out = pw

        expect:
        Model.count() == 1

        when:
        updateAction.initWith(name: "The New Model Name", type: Model.name, id: model.id.toString(), 'ext:foo': 'bar', 'ext:bar': '')
        updateAction.run()

        then:
        !updateAction.failed
        Model.count() == 1
        Model.countByName('The Model') == 0
        Model.countByName('The New Model Name') == 1
        sw.toString() == "<a href='#/catalogue/model/1'>Model 'The New Model Name'</a> updated"
        Model.findByName('The New Model Name').ext.foo == 'bar'
        Model.findByName('The New Model Name').ext.bar == null
        updateAction.result == AbstractActionRunner.encodeEntity(Model.findByName('The New Model Name'))
    }

    def "error is reported to the output stream"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        updateAction.out = pw

        when:
        updateAction.initWith(name: "x" * 300, type: Model.name, id: model.id.toString())
        updateAction.run()

        then:
        updateAction.failed
        sw.toString().startsWith('Unable to update Model:' + model.id)
    }
}
