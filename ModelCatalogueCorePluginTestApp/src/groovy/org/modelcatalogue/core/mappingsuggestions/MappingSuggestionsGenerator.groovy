package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.actions.Batch

@CompileStatic
interface MappingSuggestionsGenerator {
    void execute(Long batchId,
                 Class sourceClazz,
                 DataModel sourceDataModel,
                 Class destionationClazz,
                 DataModel destinationDataModel,
                 Float minDistance,
                 MatchAgainst matchAgainst)
}