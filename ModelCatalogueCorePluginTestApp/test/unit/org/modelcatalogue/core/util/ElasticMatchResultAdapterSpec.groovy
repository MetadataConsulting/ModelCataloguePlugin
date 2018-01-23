package org.modelcatalogue.core.util

import org.modelcatalogue.core.DataElement
import spock.lang.Specification

class ElasticMatchResultAdapterSpec extends Specification {


    def "ElasticMatchResultAdapter adapts a ElasticMatchResult into a MatchResult"() {
        given:
        DataElement dataElementA = new DataElement(name: 'dataElementA')
        dataElementA.id = 1
        DataElement dataElementB = new DataElement(name: 'dataElementB')
        dataElementB.id = 3
        ElasticMatchResult elasticMatchResult = new ElasticMatchResult(catalogueElementA: dataElementA,
                catalogueElementB: dataElementB,
                message: 'this is a message',
                matchScore: 60,
        )

        when:
        MatchResult matchResult = new ElasticMatchResultAdapter(elasticMatchResult)

        then:
        matchResult
        matchResult.message == 'this is a message'
        matchResult.matchScore == 60
        matchResult.dataElementAId == 1
        matchResult.dataElementAName == 'dataElementA'
        matchResult.dataElementBId == 3
        matchResult.dataElementBName == 'dataElementB'
    }
}
