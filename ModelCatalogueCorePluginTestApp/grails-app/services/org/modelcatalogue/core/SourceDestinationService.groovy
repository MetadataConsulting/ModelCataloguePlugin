package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.SourceDestinationEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService

@CompileStatic
class SourceDestinationService {

    DataModelGormService dataModelGormService

    CatalogueElementGormService catalogueElementGormService

    MetadataResponseEvent findSourceDestination(Long sourceId, Long destinationId) {
        CatalogueElement element = catalogueElementGormService.findById(sourceId)
        if (!element) {
            return new CatalogueElementNotFoundEvent()
        }
        if ( !dataModelGormService.isAdminOrHasAdministratorPermission(element) ) {
            return new UnauthorizedEvent()
        }

        CatalogueElement destination = catalogueElementGormService.findById(destinationId)
        if (!destination) {
            return new CatalogueElementNotFoundEvent()
        }
        if ( !dataModelGormService.isAdminOrHasAdministratorPermission(destination) ) {
            return new UnauthorizedEvent()
        }
        new SourceDestinationEvent(source: element, destination: destination)
    }
}
