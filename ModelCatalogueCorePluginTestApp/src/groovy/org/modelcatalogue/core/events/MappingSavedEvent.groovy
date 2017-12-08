package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Mapping

@CompileStatic
class MappingSavedEvent implements MetadataResponseSuccessEvent {
    Mapping mapping
}

