package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

class AuditService {

    static transactional = false

    def modelCatalogueSecurityService

    private static ThreadLocal<Auditor> auditor = new ThreadLocal<Auditor>() {
        protected Auditor initialValue() { return new DefaultAuditor(); }
    }

    /**
     * Allows to run block of code without logging any activity. This is supposed to be used when setting and reseting
     * the UPDATED state and
     * @param noAuditBlock code to be executed without logging the changes
     * @return the value returned from the noAuditBlock
     */
    static <R> R noAudit(Closure<R> noAuditBlock) {
        Auditor old = auditor.get()
        auditor.set(NoOpAuditor.INSTANCE)
        R result = noAuditBlock()
        auditor.set(old)
        return result
    }

    ListWithTotalAndType<Change> getChanges(Map params, CatalogueElement element) {
        long latestId = element.latestVersionId ?: element.id
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }
        Lists.fromCriteria(params, Change) {
            eq 'latestVersionId', latestId
        }
    }

    void logElementCreated(CatalogueElement element) {
        auditor.get().logElementCreated(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logNewMetadata(ExtensionValue extension) {
        auditor.get().logNewMetadata(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logMetadataUpdated(ExtensionValue extension) {
        auditor.get().logMetadataUpdated(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logMetadataDeleted(ExtensionValue extension) {
        auditor.get().logMetadataDeleted(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logNewRelationshipMetadata(RelationshipMetadata extension) {
        auditor.get().logNewRelationshipMetadata(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logRelationshipMetadataUpdated(RelationshipMetadata extension) {
        auditor.get().logRelationshipMetadataUpdated(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logRelationshipMetadataDeleted(RelationshipMetadata extension) {
        auditor.get().logRelationshipMetadataDeleted(extension, modelCatalogueSecurityService.currentUser?.id)
    }

    void logElementDeleted(CatalogueElement element) {
        auditor.get().logElementDeleted(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logElementUpdated(CatalogueElement element) {
        auditor.get().logElementUpdated(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logNewRelation(Relationship relationship) {
        auditor.get().logNewRelation(relationship, modelCatalogueSecurityService.currentUser?.id)
    }

    void logRelationRemoved(Relationship relationship) {
        auditor.get().logRelationRemoved(relationship, modelCatalogueSecurityService.currentUser?.id)
    }


}
