package org.modelcatalogue.core

import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@TestFor(DataModelCreateController)
class DataModelCreateCommandConstraintsSpec extends Specification {

    @Shared
    @Subject
    DataModelCreateCommand cmd

    def setupSpec() {
        cmd = new DataModelCreateCommand()
    }

    def cleanupSpec() {
        cmd = null
    }

    void 'name cannot be null'() {
        when:
        cmd.name = null

        then:
        !cmd.validate(['name'])
        cmd.errors['name'].code == 'nullable'
    }

    void 'dataModelPolicies can be null'() {
        when:
        cmd.dataModelPolicies = null

        then:
        cmd.validate(['dataModelPolicies'])
    }

    void 'description can be null'() {
        when:
        cmd.description = null

        then:
        cmd.validate(['description'])
    }

    void 'dataModelPolicies can be an empty list'() {
        when:
        cmd.dataModelPolicies = []

        then:
        cmd.validate(['dataModelPolicies'])

        when:
        cmd.dataModelPolicies = []

        then:
        cmd.validate(['dataModelPolicies'])
    }

    void 'semanticVersion can be null'() {
        when:
        cmd.semanticVersion = []

        then:
        cmd.validate(['semanticVersion'])
    }

    void 'modelCatalogueId can be null'() {
        when:
        cmd.modelCatalogueId = []

        then:
        cmd.validate(['modelCatalogueId'])
    }

    void 'dataModels can be null'() {
        when:
        cmd.dataModels = null

        then:
        cmd.validate(['dataModels'])
    }

    void 'dataModels can be an empty list'() {
        when:
        cmd.dataModels = [1]

        then:
        cmd.validate(['dataModels'])

        when:
        cmd.dataModels = []

        then:
        cmd.validate(['dataModels'])
    }
}
