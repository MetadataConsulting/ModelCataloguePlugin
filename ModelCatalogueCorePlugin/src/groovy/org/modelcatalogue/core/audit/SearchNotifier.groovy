package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.SearchCatalogue

class SearchNotifier implements Auditor {

    final SearchCatalogue searchService

    SearchNotifier(SearchCatalogue searchService) {
        this.searchService = searchService
    }

    @Override
    Long getDefaultAuthorId() {
        return null
    }

    @Override
    void setDefaultAuthorId(Long id) {

    }

    @Override
    Long getParentChangeId() {
        return null
    }

    @Override
    void setParentChangeId(Long id) {

    }

    @Override
    Boolean getSystem() {
        return null
    }

    @Override
    void setSystem(Boolean system) {

    }

    @Override
    Long logExternalChange(CatalogueElement source, String message, Long authorId) {
        searchService.index(source)
        return null
    }

    @Override
    Long logNewVersionCreated(CatalogueElement element, Long authorId) {
        searchService.index(element)
        return null
    }

    @Override
    Long logElementFinalized(CatalogueElement element, Long authorId) {
        searchService.index(element)
        return null
    }

    @Override
    Long logElementDeprecated(CatalogueElement element, Long authorId) {
        searchService.index(element)
        return null
    }

    @Override
    Long logElementCreated(CatalogueElement element, Long authorId) {
        searchService.index(element)
        return null
    }

    @Override
    Long logElementDeleted(CatalogueElement element, Long authorId) {
        searchService.unindex(element)
        return null
    }

    @Override
    Long logElementUpdated(CatalogueElement element, Long authorId) {
        searchService.index(element)
        return null
    }

    @Override
    Long logMappingCreated(Mapping mapping, Long authorId) {
        searchService.index(mapping.source)
        searchService.index(mapping.destination)
        return null
    }

    @Override
    Long logMappingDeleted(Mapping mapping, Long authorId) {
        searchService.index(mapping.source)
        searchService.index(mapping.destination)
        return null
    }

    @Override
    Long logMappingUpdated(Mapping mapping, Long authorId) {
        searchService.index(mapping.source)
        searchService.index(mapping.destination)
        return null
    }

    @Override
    Long logNewMetadata(ExtensionValue extension, Long authorId) {
        searchService.index(extension.element)
        return null
    }

    @Override
    Long logMetadataUpdated(ExtensionValue extension, Long authorId) {
        searchService.index(extension.element)
        return null
    }

    @Override
    Long logMetadataDeleted(ExtensionValue extension, Long authorId) {
        searchService.index(extension.element)
        return null
    }

    @Override
    Long logNewRelation(Relationship relationship, Long authorId) {
        searchService.index(relationship)
        return null
    }

    @Override
    Long logRelationRemoved(Relationship relationship, Long authorId) {
        searchService.unindex(relationship)
        return null
    }

    @Override
    Long logRelationArchived(Relationship relationship, Long authorId) {
        searchService.index(relationship)
        return null
    }

    @Override
    Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        searchService.index(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        searchService.index(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        searchService.index(extension.relationship)
        return null
    }
}
