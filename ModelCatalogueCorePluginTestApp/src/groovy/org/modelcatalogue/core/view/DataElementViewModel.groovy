package org.modelcatalogue.core.view

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.IdName

@CompileStatic
class DataElementViewModel {
    Long id
    String name
    Date lastUpdated
    ElementStatus status
    String modelCatalogueId
    IdName dataModel
}
