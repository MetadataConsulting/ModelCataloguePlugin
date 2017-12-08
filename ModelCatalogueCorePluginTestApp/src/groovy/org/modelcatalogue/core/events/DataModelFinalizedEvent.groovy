package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel

@CompileStatic
class DataModelFinalizedEvent implements MetadataResponseSuccessEvent {
    DataModel dataModel
}
