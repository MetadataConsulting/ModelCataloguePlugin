package org.modelcatalogue.core.view

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus

@CompileStatic
class DataModelViewModel {
    Long id
    String name
    Date lastUpdated
    ElementStatus status
    String semanticVersion
    List<AssetViewModel> assetsList
}
