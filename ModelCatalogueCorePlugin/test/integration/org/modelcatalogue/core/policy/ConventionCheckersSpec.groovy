package org.modelcatalogue.core.policy

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import spock.lang.Specification

class ConventionCheckersSpec extends AbstractIntegrationSpec {


    DataModelService dataModelService
    ElementService elementService

    DataModel complexModel

    def setup() {
        complexModel = buildComplexModel(dataModelService, elementService)
    }

    def "test regexp checker"() {
        RegexChecker checker = new RegexChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(complexModel, item, 'name', /[abc]+/)
        }

        then:
        complexModel.errors.errorCount == 231
    }

}
