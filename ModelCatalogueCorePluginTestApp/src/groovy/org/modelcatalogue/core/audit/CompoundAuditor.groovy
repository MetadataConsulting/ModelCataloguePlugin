package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import rx.Observable

class CompoundAuditor implements Auditor {

    private final List<Auditor> auditors = []

    static Auditor from(Auditor... auditors) {
        CompoundAuditor auditor = new CompoundAuditor()
        auditor.auditors.addAll(auditors.toList().unique())
        auditor
    }

    private CompoundAuditor() {}

    @Override
    Long getDefaultAuthorId() {
        return auditors.first().defaultAuthorId
    }

    @Override
    void setDefaultAuthorId(Long id) {
        auditors*.setDefaultAuthorId(id)
    }

    @Override
    Long getParentChangeId() {
        auditors ? auditors.first().parentChangeId : null
    }

    @Override
    void setParentChangeId(Long id) {
        auditors*.setParentChangeId(id)
    }

    @Override
    Boolean getSystem() {
        return auditors.first().system
    }

    @Override
    void setSystem(Boolean system) {
        auditors*.setSystem(system)
    }

    @Override
    Observable<Long> logExternalChange(CatalogueElement source, String message, Long authorId) {
        return pickFirst(auditors*.logExternalChange(source, message, authorId))
    }

    @Override
    Observable<Long> logNewVersionCreated(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logNewVersionCreated(element, authorId))
    }

    @Override
    Observable<Long> logElementFinalized(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logElementFinalized(element, authorId))
    }

    @Override
    Observable<Long> logElementDeprecated(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logElementDeprecated(element, authorId))
    }

    @Override
    Observable<Long> logElementCreated(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logElementCreated(element, authorId))
    }

    @Override
    Observable<Long> logElementDeleted(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logElementDeleted(element, authorId))
    }

    @Override
    Observable<Long> logElementUpdated(CatalogueElement element, Long authorId) {
        return pickFirst(auditors*.logElementUpdated(element, authorId))
    }

    @Override
    Observable<Long> logMappingCreated(Mapping mapping, Long authorId) {
        return pickFirst(auditors*.logMappingCreated(mapping, authorId))
    }

    @Override
    Observable<Long> logMappingDeleted(Mapping mapping, Long authorId) {
        return pickFirst(auditors*.logMappingDeleted(mapping, authorId))
    }

    @Override
    Observable<Long> logMappingUpdated(Mapping mapping, Long authorId) {
        return pickFirst(auditors*.logMappingUpdated(mapping, authorId))
    }

    @Override
    Observable<Long> logNewMetadata(ExtensionValue extension, Long authorId) {
        return pickFirst(auditors*.logNewMetadata(extension, authorId))
    }

    @Override
    Observable<Long> logMetadataUpdated(ExtensionValue extension, Long authorId) {
        return pickFirst(auditors*.logMetadataUpdated(extension, authorId))
    }

    @Override
    Observable<Long> logMetadataDeleted(ExtensionValue extension, Long authorId) {
        return pickFirst(auditors*.logMetadataDeleted(extension, authorId))
    }

    @Override
    Observable<Long> logNewRelation(Relationship relationship, Long authorId) {
        return pickFirst(auditors*.logNewRelation(relationship, authorId))
    }

    @Override
    Observable<Long> logRelationRemoved(Relationship relationship, Long authorId) {
        return pickFirst(auditors*.logRelationRemoved(relationship, authorId))
    }

    @Override
    Observable<Long> logRelationArchived(Relationship relationship, Long authorId) {
        return pickFirst(auditors*.logRelationArchived(relationship, authorId))
    }

    @Override
    Observable<Long> logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        return pickFirst(auditors*.logNewRelationshipMetadata(extension, authorId))
    }

    @Override
    Observable<Long> logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        return pickFirst(auditors*.logRelationshipMetadataUpdated(extension, authorId))
    }

    @Override
    Observable<Long> logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        return pickFirst(auditors*.logRelationshipMetadataDeleted(extension, authorId))
    }

    private static <T> T pickFirst(List<T> values) {
        List<T> filtered = values.grep()
        if (filtered) {
            return filtered.first()
        }
        return null
    }
}
