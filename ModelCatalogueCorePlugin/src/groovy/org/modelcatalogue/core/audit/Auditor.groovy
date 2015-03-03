package org.modelcatalogue.core.audit

import org.modelcatalogue.core.*

interface Auditor {

    Long getDefaultAuthorId()
    void setDefaultAuthorId(Long id)

    Long getParentChangeId()
    void setParentChangeId(Long id)

    Boolean getSystem()
    void setSystem(Boolean system)

    Long logExternalChange(CatalogueElement source, String message, Long authorId)
    Long logNewVersionCreated(CatalogueElement element, Long authorId)
    Long logElementFinalized(CatalogueElement element, Long authorId)
    Long logElementDeprecated(CatalogueElement element, Long authorId)

    Long logElementCreated(CatalogueElement element, Long authorId)
    Long logElementDeleted(CatalogueElement element, Long authorId)
    Long logElementUpdated(CatalogueElement element, Long authorId)

    Long logMappingCreated(Mapping mapping, Long authorId)
    Long logMappingDeleted(Mapping mapping, Long authorId)
    Long logMappingUpdated(Mapping mapping, Long authorId)

    Long logNewMetadata(ExtensionValue extension, Long authorId)
    Long logMetadataUpdated(ExtensionValue extension, Long authorId)
    Long logMetadataDeleted(ExtensionValue extension, Long authorId)

    Long logNewRelation(Relationship relationship, Long authorId)
    Long logRelationRemoved(Relationship relationship, Long authorId)
    Long logRelationArchived(Relationship relationship, Long authorId)

    Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId)
    Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId)
    Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId)

}