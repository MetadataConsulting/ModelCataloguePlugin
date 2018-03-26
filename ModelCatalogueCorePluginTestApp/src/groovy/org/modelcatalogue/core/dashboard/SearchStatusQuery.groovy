package org.modelcatalogue.core.dashboard

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus

@CompileStatic
class SearchStatusQuery {
    List<ElementStatus> statusList
    String search
    Boolean searchWithWhitespace
}
