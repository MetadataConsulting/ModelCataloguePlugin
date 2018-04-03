package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.persistence.DataElementGormService

import javax.annotation.PostConstruct

@CompileStatic
class MapppingSuggestionsConfigurationService {

    GrailsApplication grailsApplication
    MatchAgainst matchAgainst = MatchAgainst.CONTAINS_STEMMED_KEYWORDS
    DataElementGormService dataElementGormService
    Integer minSizeMatchAgainstContainsStemmedKeywords

    @CompileDynamic
    @PostConstruct
    private void init() {
        minSizeMatchAgainstContainsStemmedKeywords = grailsApplication.config.mc.mappingsuggestions.minSizeMatchAgainstContainsStemmedKeywords ?: 1000
        String matchAgainstConfigValue = grailsApplication.config.mc.mappingsuggestions.matchAgainst

        if ( matchAgainstConfigValue != null ) {
            try {
                matchAgainst = matchAgainstConfigValue as MatchAgainst
            } catch(IllegalArgumentException e) {
            }
        }
        log.info("matchAgainst =" + matchAgainst )
    }

    MatchAgainst matchAgainstDependingOnDataModelSize(DataModel dataModel) {
        if (
            matchAgainst == MatchAgainst.CONTAINS_STEMMED_KEYWORDS &&
            dataElementGormService.countByDataModel(dataModel) < minSizeMatchAgainstContainsStemmedKeywords
        ) {
            return MatchAgainst.ALL
        }
        return matchAgainst
    }
}
