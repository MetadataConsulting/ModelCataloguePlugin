package org.modelcatalogue.core.audit

import org.modelcatalogue.core.*

/**
 * Auditor for the situation we want to disable auditing such as restoring element state.
 * Pay attention that all saves must flush as otherwise the auditing may happen outside the silenced scope.
 */
enum NoOpAuditor implements Auditor {

    INSTANCE;

    @Override Long getDefaultAuthorId() { null }

    @Override void setDefaultAuthorId(Long id) { }

    @Override void logElementCreated(CatalogueElement element, Long authorId) { }

    @Override void logElementDeleted(CatalogueElement element, Long authorId) { }

    @Override void logElementUpdated(CatalogueElement element, Long authorId) { }

    @Override void logMappingCreated(Mapping mapping, Long authorId) { }

    @Override void logMappingDeleted(Mapping mapping, Long authorId) { }

    @Override void logMappingUpdated(Mapping mapping, Long authorId) { }

    @Override void logNewMetadata(ExtensionValue extension, Long authorId) { }

    @Override void logMetadataUpdated(ExtensionValue extension, Long authorId) { }

    @Override void logMetadataDeleted(ExtensionValue extension, Long authorId) { }

    @Override void logNewRelation(Relationship relationship, Long authorId) { }

    @Override void logRelationRemoved(Relationship relationship, Long authorId) { }

    @Override void logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) { }

    @Override void logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) { }

    @Override void logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) { }


}
