package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel

@CompileStatic
class DataModelFoundEvent implements MetadataResponseSuccessEvent {
    DataModel dataModel
}
