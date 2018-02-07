package org.modelcatalogue.core.actions

import groovy.transform.CompileStatic

@CompileStatic
enum OptimizationType {
    DATA_ELEMENT_EXACT_MATCH,
    DATA_ELEMENT_FUZZY_MATCH,
    ENUM_DUPLICATES_AND_SYNOYMS,
    DATA_ELEMENT_FULL_TEXT_MATCH
}
