package org.modelcatalogue.core

class RelationshipControllerIntegrationSpec extends AbstractIntegrationSpec {

    def setup() {
        loadMarshallers()
        loadFixtures()
    }

    def "restore relationship"() {
        def controller = new RelationshipController()

        Relationship relationship = new Relationship(
                source: DataType.findByName('boolean'),
                destination: DataType.findByName('integer'),
                relationshipType: RelationshipType.relatedToType,
                archived: true
        )
        relationship.save(failOnError: true)

        expect:
        relationship.errors.errorCount == 0
        relationship.archived

        when:
        controller.request.method = "POST"
        controller.request.format = "json"
        controller.params.id = '' + relationship.id
        controller.restore()

        def response = controller.response.json

        then:
        response
        response.archived == false

        cleanup:
        relationship.delete(flush: true)

    }

}
