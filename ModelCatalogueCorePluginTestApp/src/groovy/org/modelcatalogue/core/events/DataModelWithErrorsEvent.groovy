package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel

@CompileStatic
class DataModelWithErrorsEvent implements MetadataResponseFailureEvent {
    DataModel dataModel
}
