package org.modelcatalogue.core.policy

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Metadata

class ConventionCheckersSpec extends AbstractIntegrationSpec {


    DataModelService dataModelService
    ElementService elementService

    DataModel complexModel

    def setup() {
        complexModel = buildComplexModel(dataModelService, elementService)
    }

    def "test regexp checker - property"() {
        ConventionChecker checker = new RegexChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(complexModel, CatalogueElement, item, 'name', /[abc]+/, null)
        }

        then:
        complexModel.errors.errorCount == 231
    }

    def "test regexp checker - extension"() {
        ConventionChecker checker = new RegexChecker()

        when:
        checker.check(complexModel, CatalogueElement, complexModel, "ext[${Metadata.AUTHORS}]", /Author Two.*/, null)

        then:
        complexModel.errors.errorCount == 1
    }

    def "test required checker - property"() {
        ConventionChecker checker = new RequiredChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(complexModel, CatalogueElement, item, 'description', null, null)
        }

        then:
        complexModel.errors.errorCount == 1
    }

    def "test required checker - extension"() {
        ConventionChecker checker = new RequiredChecker()

        when:
        checker.check(complexModel, CatalogueElement, complexModel, "ext[no.such.ext]", null, null)

        then:
        complexModel.errors.errorCount == 1
    }

    def "test unique checker - property"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)
        ConventionChecker checker = new UniqueChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(complexModel, CatalogueElement, item, 'name', null, null)
        }

        then:
        complexModel.errors.errorCount == 2
    }

    def "checkers are loaded"() {
        expect:
        Conventions.checkers.any { it.key == 'unique'}
        Conventions.checkers.any { it.key == 'required'}
        Conventions.checkers.any { it.key == 'regex'}
    }

    def "verify policy"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)

        Policy policy = PolicyBuilder.build {
            check every property 'name' apply regex: /[abc]+/
            check dataClass property 'name' is unique
        }

        when:
        policy.verify(complexModel)

        then:
        complexModel.errors.errorCount == 234
    }

    def "verify policy - string"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)

        Policy policy = PolicyBuilder.build """
            check every property 'name' apply regex: /[abc]+/
            check dataClass property 'name' is unique
        """

        when:
        policy.verify(complexModel)

        then:
        complexModel.errors.errorCount == 234
    }

}
