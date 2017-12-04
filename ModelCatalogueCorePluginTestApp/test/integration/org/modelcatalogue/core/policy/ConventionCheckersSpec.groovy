package org.modelcatalogue.core.policy

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.util.Metadata
import spock.lang.IgnoreIf

@IgnoreIf( { System.getProperty('spock.ignore.slow') })
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
            checker.check(VerificationPhase.FINALIZATION_CHECK, complexModel, CatalogueElement, item, 'name', /[abc]+/, null, false)
        }

        then:
        complexModel.errors.errorCount == 231
    }

    def "test regexp checker - extension"() {
        ConventionChecker checker = new RegexChecker()

        when:
        checker.check(VerificationPhase.EXTENSIONS_CHECK, complexModel, CatalogueElement, complexModel, "ext[${Metadata.AUTHORS}]", /Author Two.*/, null, false)

        then:
        complexModel.errors.errorCount == 1
    }

    def "test required checker - property"() {
        ConventionChecker checker = new RequiredChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(VerificationPhase.FINALIZATION_CHECK, complexModel, CatalogueElement, item, 'description', null, null, false)
        }

        then:
        complexModel.errors.errorCount == 1
    }

    def "test required checker - extension"() {
        ConventionChecker checker = new RequiredChecker()

        when:
        checker.check(VerificationPhase.EXTENSIONS_CHECK, complexModel, CatalogueElement, complexModel, "ext[no.such.ext]", null, null, false)

        then:
        complexModel.errors.errorCount == 1
    }

    def "test unique checker - property"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)
        ConventionChecker checker = new UniqueChecker()

        when:
        for (CatalogueElement item in complexModel.declares) {
            checker.check(VerificationPhase.FINALIZATION_CHECK, complexModel, CatalogueElement, item, 'name', null, null, false)
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
        policy.verifyAll(complexModel)

        then:
        complexModel.errors.errorCount == 235
    }

    def "verify policy - string"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)

        Policy policy = PolicyBuilder.build """
            check every property 'name' apply regex: '[abc]+'
            check dataClass property 'name' is unique
        """

        when:
        policy.verifyAll(complexModel)

        then:
        complexModel.errors.errorCount == 235
    }

    def "print policy - string"() {
        new DataClass(dataModel: complexModel, name: "C4CTDE Model 1").save(failOnError: true)

        String original = """
            |check every property 'name' apply regex: '[abc]+' otherwise 'You can only use abc letters in the name of {2}'
            |check dataClass property 'name' is 'unique'
        """.stripMargin().trim()

        Policy policy = PolicyBuilder.build original

        when:
        String printed = policy.toString()

        then:
        printed == original
    }

}
