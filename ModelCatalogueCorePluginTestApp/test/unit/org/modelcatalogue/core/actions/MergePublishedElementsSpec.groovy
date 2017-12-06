package org.modelcatalogue.core.actions

import static org.modelcatalogue.core.actions.AbstractActionRunner.encodeEntity
import static org.modelcatalogue.core.actions.AbstractActionRunner.normalizeDescription
import grails.test.mixin.Mock
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import spock.lang.Ignore
import spock.lang.Specification

@Mock([DataClass, ExtensionValue])
class MergePublishedElementsSpec extends Specification {

    MergePublishedElements merge = new MergePublishedElements()

    DataClass one
    DataClass two

    def setup() {
        one = new DataClass(name: 'one').save(failOnError: true)
        two = new DataClass(name: 'two').save(failOnError: true)

        merge.autowireCapableBeanFactory = Mock(AutowireCapableBeanFactory)
        merge.relationshipService = Mock(RelationshipService)
        merge.elementService = Mock(ElementService)

    }

    def "uses default action natural name"() {
       expect:
        merge.naturalName == "Merge Published Elements"
        merge.description == normalizeDescription(MergePublishedElements.description)

        when:
        merge.initWith(source: encodeEntity(one), destination: encodeEntity(two))

        then:
        merge.message == """Merge Data Class <a target="_blank" href="#/catalogue/dataClass/${one.id}">${one.name}</a> into Data Class <a target="_blank" href="#/catalogue/dataClass/${two.id}">${two.name}</a> including all related elements having at least one data model as the source""".stripIndent().trim()
    }


    def "the action validates the parameters"() {
        Map<String, String> errorsForEmpty = merge.validate([:])

        expect:
        errorsForEmpty.containsKey 'source'
        errorsForEmpty.containsKey 'destination'

        when:
        Map<String, String> errorsForNonExisting = merge.validate(source: 'gorm://org.modelacatalogue.core.DataClass:1233456')

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
        1 * merge.elementService.merge(one, two) >> one
    }

    def "published element service is called with the proper parameters"() {
        StringWriter sw = []
        PrintWriter pw = [sw]

        merge.out = pw

        when:
        merge.initWith source: encodeEntity(one), destination: encodeEntity(two)
        merge.run()

        then:
        1 * merge.elementService.merge(one, two) >> one
        !merge.failed
        sw.toString() == """Merged Data Class <a target="_blank" href="#/catalogue/dataClass/${one.id}">${one.name}</a> into Data Class <a target="_blank" href="#/catalogue/dataClass/${two.id}">${two.name}</a>"""
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
