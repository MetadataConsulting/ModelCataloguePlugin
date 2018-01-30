package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic

@CompileStatic
enum MatchAgainst {
    ALL,
    CONTAINS_STEMMED_KEYWORDS,
    ELASTIC_SEARCH
}