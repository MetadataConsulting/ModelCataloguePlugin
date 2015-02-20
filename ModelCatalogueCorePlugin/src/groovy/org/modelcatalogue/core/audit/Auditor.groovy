package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata

interface Auditor {

    void logElementCreated(CatalogueElement element, Long authorId)
    void logElementDeleted(CatalogueElement element, Long authorId)
    void logElementUpdated(CatalogueElement element, Long authorId)

    void logNewMetadata(ExtensionValue extension, Long authorId)
    void logMetadataUpdated(ExtensionValue extension, Long authorId)
    void logMetadataDeleted(ExtensionValue extension, Long authorId)

    void logNewRelation(Relationship relationship, Long authorId)
    void logRelationRemoved(Relationship relationship, Long authorId)

    void logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId)
    void logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId)
    void logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId)

}