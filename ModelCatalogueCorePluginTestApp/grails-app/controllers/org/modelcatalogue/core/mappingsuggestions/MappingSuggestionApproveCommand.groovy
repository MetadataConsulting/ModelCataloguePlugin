package org.modelcatalogue.core.mappingsuggestions

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@Validateable
@GrailsCompileStatic
class MappingSuggestionApproveCommand {
    List<Long> mappingSuggestionIds
    Long batchId
    static constraints = {
        batchId nullable: false
        mappingSuggestionIds nullable: false, minSize: 1
    }
}