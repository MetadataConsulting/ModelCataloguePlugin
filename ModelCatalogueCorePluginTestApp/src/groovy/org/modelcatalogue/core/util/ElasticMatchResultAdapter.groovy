package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class ElasticMatchResultAdapter implements MatchResult {

    ElasticMatchResult elasticMatchResult

    ElasticMatchResultAdapter(ElasticMatchResult elasticMatchResult) {
        this.elasticMatchResult = elasticMatchResult
    }

    @Override
    String getDataElementAName() {
        elasticMatchResult?.catalogueElementA?.name
    }

    @Override
    Long getDataElementAId() {
        elasticMatchResult?.catalogueElementA?.id
    }

    @Override
    String getDataElementBName() {
        elasticMatchResult?.catalogueElementB?.name
    }

    @Override
    Long getDataElementBId() {
        elasticMatchResult?.catalogueElementB?.id
    }

    @Override
    Float getMatchScore() {
        elasticMatchResult?.matchScore
    }

    @Override
    String getMessage() {
        elasticMatchResult?.message
    }
}
