package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.audit.Auditor

class ElasticSearchServiceNotifier implements Auditor {

    final ElasticSearchService elasticSearchService

    ElasticSearchServiceNotifier(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService
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
        elasticSearchService.index(source)
        return null
    }

    @Override
    Long logNewVersionCreated(CatalogueElement element, Long authorId) {
        elasticSearchService.index(element)
        return null
    }

    @Override
    Long logElementFinalized(CatalogueElement element, Long authorId) {
        elasticSearchService.index(element)
        return null
    }

    @Override
    Long logElementDeprecated(CatalogueElement element, Long authorId) {
        elasticSearchService.index(element)
        return null
    }

    @Override
    Long logElementCreated(CatalogueElement element, Long authorId) {
        elasticSearchService.index(element)
        return null
    }

    @Override
    Long logElementDeleted(CatalogueElement element, Long authorId) {
        elasticSearchService.unindex(element)
        return null
    }

    @Override
    Long logElementUpdated(CatalogueElement element, Long authorId) {
        elasticSearchService.index(element)
        return null
    }

    @Override
    Long logMappingCreated(Mapping mapping, Long authorId) {
        elasticSearchService.index(mapping.source)
        elasticSearchService.index(mapping.destination)
        return null
    }

    @Override
    Long logMappingDeleted(Mapping mapping, Long authorId) {
        elasticSearchService.index(mapping.source)
        elasticSearchService.index(mapping.destination)
        return null
    }

    @Override
    Long logMappingUpdated(Mapping mapping, Long authorId) {
        elasticSearchService.index(mapping.source)
        elasticSearchService.index(mapping.destination)
        return null
    }

    @Override
    Long logNewMetadata(ExtensionValue extension, Long authorId) {
        elasticSearchService.index(extension.element)
        return null
    }

    @Override
    Long logMetadataUpdated(ExtensionValue extension, Long authorId) {
        elasticSearchService.index(extension.element)
        return null
    }

    @Override
    Long logMetadataDeleted(ExtensionValue extension, Long authorId) {
        elasticSearchService.index(extension.element)
        return null
    }

    @Override
    Long logNewRelation(Relationship relationship, Long authorId) {
        elasticSearchService.index(relationship)
        return null
    }

    @Override
    Long logRelationRemoved(Relationship relationship, Long authorId) {
        elasticSearchService.unindex(relationship)
        return null
    }

    @Override
    Long logRelationArchived(Relationship relationship, Long authorId) {
        elasticSearchService.index(relationship)
        return null
    }

    @Override
    Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        elasticSearchService.index(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        elasticSearchService.index(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        elasticSearchService.index(extension.relationship)
        return null
    }
}
