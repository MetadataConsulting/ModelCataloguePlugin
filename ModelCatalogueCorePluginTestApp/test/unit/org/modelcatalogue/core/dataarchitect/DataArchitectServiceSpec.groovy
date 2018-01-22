package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.TestFor
import org.modelcatalogue.core.util.MatchResult
import org.modelcatalogue.core.util.MatchResultImpl
import org.modelcatalogue.core.util.MetadataDomain
import spock.lang.Specification

@TestFor(DataArchitectService)
class DataArchitectServiceSpec extends Specification {

    void "matchParams method results expected map"() {
        given:

        Map<String, String> expected = [:]
        expected.put("""source""", """gorm://org.modelcatalogue.core.DataElement:2""")
        expected.put("""destination""", """gorm://org.modelcatalogue.core.DataElement:4""")
        expected.put("""type""", """gorm://org.modelcatalogue.core.RelationshipType:5""")
        expected.put("""matchScore""", """98""")
        expected.put("""matchOn""", """ElementName""")
        expected.put("""message""", """test match""")

        when:
        Map<String, String> result = service.matchParams(2, MetadataDomain.DATA_ELEMENT, 4, MetadataDomain.DATA_ELEMENT, 5, 98, 'test match')

        then:
        expected.keySet() == result.keySet()
        expected.source == result.source
        expected.destination == result.destination
        expected.type == result.type
        expected.matchScore == result.matchScore
        expected.matchOn == result.matchOn
        expected.message == result.message
    }

    void "matchParams with MatchResult parameter results expected map"() {
        given:

        Map<String, String> expected = [:]
        expected.put("""source""", """gorm://org.modelcatalogue.core.DataElement:2""")
        expected.put("""destination""", """gorm://org.modelcatalogue.core.DataElement:4""")
        expected.put("""type""", """gorm://org.modelcatalogue.core.RelationshipType:5""")
        expected.put("""matchScore""", """98""")
        expected.put("""matchOn""", """ElementName""")


        when:
        MatchResult matchResult = new MatchResultImpl(dataElementAId: 2, dataElementBId: 4, matchScore: 98.0)
        Map<String, String> result = service.matchParams(matchResult, MetadataDomain.DATA_ELEMENT, MetadataDomain.DATA_ELEMENT, 5)

        then:
        expected.keySet() == result.keySet()
        expected.source == result.source
        expected.destination == result.destination
        expected.type == result.type
        expected.matchScore == result.matchScore
        expected.matchOn == result.matchOn
        expected.message == result.message



    }
}
