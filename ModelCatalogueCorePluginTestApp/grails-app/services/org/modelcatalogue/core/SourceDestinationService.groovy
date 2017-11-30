package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.events.CatalogueElementFound
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.SourceDestinationEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.util.DestinationClass

@CompileStatic
class SourceDestinationService {

    DataModelAclService dataModelAclService

    CatalogueElementGormService catalogueElementGormService

    MetadataResponseEvent findSourceDestination(Long sourceId, Long destinationId) {
        MetadataResponseEvent sourceEvent = findCatalogueElementById(sourceId)
        if ( !(sourceEvent instanceof CatalogueElementFound) ) {
            return sourceEvent
        }
        MetadataResponseEvent destinationEvent = findCatalogueElementById(destinationId)
        if ( !(destinationEvent instanceof CatalogueElementFound) ) {
            return destinationEvent
        }
        CatalogueElement source = (sourceEvent as CatalogueElementFound).catalogueElement
        CatalogueElement destination = (destinationEvent as CatalogueElementFound).catalogueElement
        new SourceDestinationEvent(source: source, destination: destination)
    }

    MetadataResponseEvent findCatalogueElementById(Long catalogueElementId) {
        CatalogueElement element = catalogueElementGormService.findById(catalogueElementId)
        if (!element) {
            return new CatalogueElementNotFoundEvent()
        }
        if ( !dataModelAclService.isAdminOrHasAdministratorPermission(element) ) {
            return new UnauthorizedEvent()
        }
        new CatalogueElementFound(catalogueElement: element)
    }

    MetadataResponseEvent findCatalogueElementByDestinationClass(DestinationClass destination) {
        Class destinationClass
        try {
            destinationClass = Class.forName(destination.className)
        } catch (ClassNotFoundException ignored) {
            return new CatalogueElementNotFoundEvent()
        }
        CatalogueElement element = catalogueElementGormService.findCatalogueElementByClassAndId(destinationClass, destination.id)
        if (!element) {
            return new CatalogueElementNotFoundEvent()
        }
        if ( !dataModelAclService.isAdminOrHasAdministratorPermission(element) ) {
            return new UnauthorizedEvent()
        }
        new CatalogueElementFound(catalogueElement: element)
    }


    MetadataResponseEvent findSourceDestination(Long sourceId, DestinationClass destinationClass) {
        MetadataResponseEvent sourceEvent = findCatalogueElementById(sourceId)
        if ( !(sourceEvent instanceof CatalogueElementFound) ) {
            return sourceEvent
        }
        MetadataResponseEvent destinationEvent = findCatalogueElementByDestinationClass(destinationClass)
        if ( !(destinationEvent instanceof CatalogueElementFound) ) {
            return destinationEvent
        }
        CatalogueElement source = (sourceEvent as CatalogueElementFound).catalogueElement
        CatalogueElement destination = (destinationEvent as CatalogueElementFound).catalogueElement
        new SourceDestinationEvent(source: source, destination: destination)
    }
}
