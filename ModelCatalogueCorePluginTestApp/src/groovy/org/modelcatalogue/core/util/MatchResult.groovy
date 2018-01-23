package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement

@CompileStatic
interface MatchResult {
    String getDataElementAName()
    Long getDataElementAId()
    String getDataElementBName()
    Long getDataElementBId()
    Float getMatchScore()
    String getMessage()
}
