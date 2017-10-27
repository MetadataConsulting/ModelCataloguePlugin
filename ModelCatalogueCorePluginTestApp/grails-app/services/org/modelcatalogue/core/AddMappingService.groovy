package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.events.MappingSavedEvent
import org.modelcatalogue.core.events.MappingWithErrorsEvent
import org.modelcatalogue.core.events.MetadataResponseEvent

@CompileStatic
class AddMappingService {

    MappingService mappingService

    MetadataResponseEvent add(CatalogueElement source, CatalogueElement destination, String mappingString) {
        Mapping mapping = mappingService.map(source, destination, mappingString)
        if (mapping.hasErrors()) {
            return new MappingWithErrorsEvent(mapping: mapping)
        }
        new MappingSavedEvent(mapping: mapping)
    }
}
