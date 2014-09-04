package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@Mock(Classification)
class ClassificationSpec extends Specification {

    @Unroll
    void "#name validates=#validates"() {
        Classification classification = new Classification(name: name)
        classification.beforeInsert()
        classification.save()

        expect:
        validates == !classification.hasErrors()

        where:
        validates | name
        true      | "foo"
        false     | "All"
        true      | "AlL"
        false     | "catalogue"
        false     | "Catalogue"
        true      | "CataLoguE"

    }
}
