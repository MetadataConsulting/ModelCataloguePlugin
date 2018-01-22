package org.modelcatalogue.core.mappingsuggestions

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.modelcatalogue.core.actions.ActionState

@Validateable
@GrailsCompileStatic
class MappingSuggestionIndexCommand  {
    Long batchId
    Integer max
    Integer offset
    List<ActionState> status
    Integer score
    String term

    static constraints = {
        term nullable: true
        batchId nullable: false
        max nullable: true, min: 1
        score nullable: true, range: 0..100
        offset nullable: true, min: 0
        status nullable: true, validator: { List<String> val, MappingSuggestionIndexCommand obj ->
            ActionState[] validStatusList = ActionState.values()
            val == null || val.every { validStatusList.contains(it) }
        }
    }
}