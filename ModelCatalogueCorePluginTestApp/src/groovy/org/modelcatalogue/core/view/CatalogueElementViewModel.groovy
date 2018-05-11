package org.modelcatalogue.core.view

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain

@EqualsAndHashCode
@CompileStatic
class CatalogueElementViewModel {
    Long id
    MetadataDomain domain
    String name
    Date lastUpdated
    ElementStatus status
    String modelCatalogueId
    IdName dataModel
}
