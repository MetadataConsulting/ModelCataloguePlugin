package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class MatchResultImpl implements MatchResult {
    String dataElementAName
    Long dataElementAId
    String dataElementBName
    Long dataElementBId
    Float matchScore

    @Override
    String getMessage() {
        null
    }
}
