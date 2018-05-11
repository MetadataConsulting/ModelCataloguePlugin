package org.modelcatalogue.core.dashboard

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.search.KeywordMatchType
import org.modelcatalogue.core.util.MetadataDomain

@CompileStatic
class SearchQuery {
    Long dataModelId
    SearchScope searchScope
    MetadataDomain metadataDomain
    List<ElementStatus> statusList
    String search
    KeywordMatchType keywordMatchType = KeywordMatchType.KEYWORDS_MATCH
    List<SearchCatalogueElementScope> searchCatalogueElementScopeList = [SearchCatalogueElementScope.ALL]
}
