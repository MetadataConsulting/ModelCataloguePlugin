package org.modelcatalogue.core.actions

import grails.test.mixin.Mock
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementService
import org.modelcatalogue.core.Relationship
import spock.lang.Ignore
import spock.lang.Specification

import static org.modelcatalogue.core.actions.AbstractActionRunner.encodeEntity
import static org.modelcatalogue.core.actions.AbstractActionRunner.normalizeDescription

@Mock([Model, ExtensionValue])
class MergePublishedElementsSpec extends Specification {

    MergePublishedElements merge = new MergePublishedElements()

    Model one
    Model two

    def setup() {
        one = new Model(name: 'one').save(failOnError: true)
        two = new Model(name: 'two').save(failOnError: true)

        merge.autowireCapableBeanFactory = Mock(org.springframework.beans.factory.config.AutowireCapableBeanFactory)

        merge.publishedElementService = Mock(PublishedElementService)
    }

    def "uses default action natural name"() {
       expect:
        merge.naturalName == "Merge Published Elements"
        merge.description == normalizeDescription(MergePublishedElements.description)

        when:
        merge.initWith(source: encodeEntity(one), destination: encodeEntity(two))

        then:
        merge.message == """Merge Model <a target="_blank" href="#/catalogue/model/${one.id}">${one.name}</a> into Model <a target="_blank" href="#/catalogue/model/${two.id}">${two.name}</a> including all related elements having at least one classification as the source""".stripIndent().trim()
    }


    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = merge.validate([:])

        expect:
        errorsForEmpty.containsKey 'source'
        errorsForEmpty.containsKey 'destination'

        when:
        Map<String, String> errorsForNonExisting = merge.validate(source: 'gorm://org.modelacatalogue.core.Model:1233456')

        then:
        errorsForNonExisting.containsKey 'source'
        errorsForNonExisting.containsKey 'destination'


        when:
        Map<String, String> errorsForCorrect = merge.validate(source: encodeEntity(one), destination: encodeEntity(two))

        then:
        errorsForCorrect.isEmpty()
    }

    def "the is action initialized with the parameters"() {
        when:
        merge.merge()

        then:
        thrown IllegalStateException

        when:
        merge.initWith source: encodeEntity(one), destination: encodeEntity(two)
        merge.merge()

        then:
        1 * merge.publishedElementService.merge(one, two) >> one
    }

    def "published element service is called with the proper parameters"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        merge.out = pw

        when:
        merge.initWith source: encodeEntity(one), destination: encodeEntity(two)
        merge.run()

        then:
        1 * merge.publishedElementService.merge(one, two) >> one
        !merge.failed
        sw.toString() == """Merged Model <a target="_blank" href="#/catalogue/model/${one.id}">${one.name}</a> into Model <a target="_blank" href="#/catalogue/model/${two.id}">${two.name}</a>"""
        merge.result == encodeEntity(one)
    }

    @Ignore
    def "error is reported to the output stream"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        merge.out = pw

        expect:
        Relationship.count() == 0

        when:
        merge.initWith(source: encodeEntity(one), destination: encodeEntity(two))
        merge.run()

        then:
        merge.failed
        sw.toString().startsWith('Unable to merge')
    }
}
