package org.modelcatalogue.core.audit

import org.modelcatalogue.core.*
import rx.Observable

interface Auditor {

    Long getDefaultAuthorId()
    void setDefaultAuthorId(Long id)

    Long getParentChangeId()
    void setParentChangeId(Long id)

    Boolean getSystem()
    void setSystem(Boolean system)

    Observable<Long> logExternalChange(CatalogueElement source, String message, Long authorId)
    Observable<Long> logNewVersionCreated(CatalogueElement element, Long authorId)
    Observable<Long> logElementFinalized(CatalogueElement element, Long authorId)
    Observable<Long> logElementDeprecated(CatalogueElement element, Long authorId)
    Observable<Long> logElementCreated(CatalogueElement element, Long authorId)
    Observable<Long> logElementDeleted(CatalogueElement element, Long authorId)
    Observable<Long> logElementUpdated(CatalogueElement element, Long authorId)
    Observable<Long> logMappingCreated(Mapping mapping, Long authorId)
    Observable<Long> logMappingDeleted(Mapping mapping, Long authorId)
    Observable<Long> logMappingUpdated(Mapping mapping, Long authorId)
    Observable<Long> logNewMetadata(ExtensionValue extension, Long authorId)
    Observable<Long> logMetadataUpdated(ExtensionValue extension, Long authorId)
    Observable<Long> logMetadataDeleted(ExtensionValue extension, Long authorId)
    Observable<Long> logNewRelation(Relationship relationship, Long authorId)
    Observable<Long> logRelationRemoved(Relationship relationship, Long authorId)
    Observable<Long> logRelationArchived(Relationship relationship, Long authorId)
    Observable<Long> logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId)
    Observable<Long> logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId)
    Observable<Long> logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId)
}
