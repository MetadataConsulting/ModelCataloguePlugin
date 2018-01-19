package org.modelcatalogue.core.actions

import grails.validation.Validateable

@Validateable
class GenerateSuggestionsCommand {
    OptimizationType optimizationType
    Long dataModel1ID
    Long dataModel2ID
    Integer minScore
    static constraints = {
        dataModel1ID nullable: false, blank: false
        dataModel2ID nullable: false, blank: false
        minScore nullable: false, range: 0..100
    }
}
