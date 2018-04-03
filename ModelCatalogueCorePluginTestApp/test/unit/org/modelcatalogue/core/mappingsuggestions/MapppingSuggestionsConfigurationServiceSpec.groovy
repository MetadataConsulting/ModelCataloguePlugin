package org.modelcatalogue.core.mappingsuggestions

import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.DataElementGormService
import spock.lang.Specification

@TestFor(MapppingSuggestionsConfigurationService)
class MapppingSuggestionsConfigurationServiceSpec extends Specification {

    def "CONTAINS_STEMMED_KEYWORDS if number of elements in data models is more than minSizeMatchAgainstContainsStemmedKeywords"() {
        given:
        service.matchAgainst = MatchAgainst.CONTAINS_STEMMED_KEYWORDS
        service.minSizeMatchAgainstContainsStemmedKeywords = 100
        service.dataElementGormService = Stub(DataElementGormService) {
            countByDataModel(_) >> 101
        }
        expect:
        MatchAgainst.CONTAINS_STEMMED_KEYWORDS == service.matchAgainstDependingOnDataModelSize(null)
    }

    def "ALL if number of elements in data models is less than minSizeMatchAgainstContainsStemmedKeywords"() {
        given:
        service.matchAgainst = MatchAgainst.CONTAINS_STEMMED_KEYWORDS
        service.minSizeMatchAgainstContainsStemmedKeywords = 100
        service.dataElementGormService = Stub(DataElementGormService) {
            countByDataModel(_) >> 90
        }
        expect:
        MatchAgainst.ALL == service.matchAgainstDependingOnDataModelSize(null)
    }

    def "if matchAgainst is configured to use ELASTIC_SEARCH matchAgainstDependingOnDataModelSize returns ELASTIC_SEARCH"() {
        given:
        service.matchAgainst = MatchAgainst.ELASTIC_SEARCH
        service.minSizeMatchAgainstContainsStemmedKeywords = 100
        service.dataElementGormService = Stub(DataElementGormService) {
            countByDataModel(_) >> 90
        }
        expect:
        MatchAgainst.ELASTIC_SEARCH == service.matchAgainstDependingOnDataModelSize(null)
    }

    def "if matchAgainst is configured to use ALL matchAgainstDependingOnDataModelSize returns ALL"() {
        given:
        service.matchAgainst = MatchAgainst.ALL
        service.minSizeMatchAgainstContainsStemmedKeywords = 100
        service.dataElementGormService = Stub(DataElementGormService) {
            countByDataModel(_) >> 90
        }
        expect:
        MatchAgainst.ALL == service.matchAgainstDependingOnDataModelSize(null)
    }
}
