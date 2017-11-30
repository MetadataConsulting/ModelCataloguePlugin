package org.modelcatalogue.core.datamodel

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus

@CompileStatic
class DataModelRow {
    Long id
    String name
    String semanticVersion
    ElementStatus status
}
