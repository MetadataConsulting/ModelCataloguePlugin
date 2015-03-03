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

    @Override Long getParentChangeId() { null }

    @Override void setParentChangeId(Long id) { }

    @Override Long logNewVersionCreated(CatalogueElement element, Long authorId) { null }

    @Override Long logElementCreated(CatalogueElement element, Long authorId) { null }

    @Override Long logElementDeleted(CatalogueElement element, Long authorId) {
        // cannot ignore deleting element, could not render changes otherwise
        new DefaultAuditor().logElementDeleted(element, authorId)
    }

    @Override Long logElementUpdated(CatalogueElement element, Long authorId) { null }

    @Override Long logMappingCreated(Mapping mapping, Long authorId) { null }

    @Override Long logMappingDeleted(Mapping mapping, Long authorId) { null }

    @Override Long logMappingUpdated(Mapping mapping, Long authorId) { null }

    @Override Long logNewMetadata(ExtensionValue extension, Long authorId) { null }

    @Override Long logMetadataUpdated(ExtensionValue extension, Long authorId) { null }

    @Override Long logMetadataDeleted(ExtensionValue extension, Long authorId) { null }

    @Override Long logNewRelation(Relationship relationship, Long authorId) { null }

    @Override Long logRelationRemoved(Relationship relationship, Long authorId) { null }

    @Override Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) { null }

    @Override Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) { null }

    @Override Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) { null }


}
