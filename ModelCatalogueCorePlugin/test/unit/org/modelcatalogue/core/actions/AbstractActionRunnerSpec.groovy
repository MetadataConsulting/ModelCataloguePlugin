package org.modelcatalogue.core.actions

import grails.test.mixin.TestFor
import org.modelcatalogue.core.MeasurementUnit
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import spock.lang.Specification

@TestFor(MeasurementUnit)
class AbstractActionRunnerSpec extends Specification {

    def "it normalizes to natural line breaks"() {
        expect:
        AbstractActionRunner.normalizeDescription("""
            following break will be replaced with space
            as you want to keep paragraphs together.

            But two new lines are interpreted as one new line
                as well as if you indent
        """) == """
            following break will be replaced with space as you want to keep paragraphs together.
            But two new lines are interpreted as one new line
                as well as if you indent
        """.stripIndent().trim()

    }

    def "encode entity as string"() {
        MeasurementUnit el = new MeasurementUnit(name: "Degree of Gray").save(failOnError: true)

        String encoded = AbstractActionRunner.encodeEntity(el)

        expect:
        encoded == "gorm://org.modelcatalogue.core.MeasurementUnit:${el.id}"
    }

    def "decode entity"() {
        MeasurementUnit el = new MeasurementUnit(name: "Degree of Gray").save(failOnError: true)

        def runner = new AbstractActionRunner() {
            @Override
            void run() {}
        }

        runner.autowireCapableBeanFactory = Mock(AutowireCapableBeanFactory)

        def encoded = runner.decodeEntity("gorm://org.modelcatalogue.core.MeasurementUnit:${el.id}")

        expect:
        encoded == el
    }

}
