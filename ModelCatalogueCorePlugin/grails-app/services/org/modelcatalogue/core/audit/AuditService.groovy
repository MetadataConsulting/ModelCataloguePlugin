package org.modelcatalogue.core.audit

import grails.util.Holders
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

class AuditService {

    static transactional = false

    def modelCatalogueSecurityService

    private static ThreadLocal<Auditor> auditor = new ThreadLocal<Auditor>() {
        protected Auditor initialValue() { return new DefaultAuditor(); }
    }

    /**
     * Allows to run block of code without logging any activity. This is supposed to be used when setting and resetting
     * the UPDATED state and creating drafts.
     *
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


    /**
     * Allows to run block as given author if no author is available. This is supposed to be used to set the author
     * for actions happening in separate thread.
     * @param defaultAuthorId default author id to be used if no author is known
     * @param withDefaultAuthorBlock code to be executed with default author
     * @return the value returned from the withDefaultAuthor
     */
    static <R> R withDefaultAuthorId(Long defaultAuthorId, Closure<R> withDefaultAuthorBlock) {
        Auditor auditor = auditor.get()
        Long currentDefault = auditor.defaultAuthorId
        auditor.defaultAuthorId = defaultAuthorId
        R result = withDefaultAuthorBlock()
        auditor.defaultAuthorId = currentDefault
        return result
    }



    /**
     * Allows to run block with the parent id assigned. This is supposed tobe used to set the parent id for actions
     * which contains a multiple atomic changes such as creating the draft.
     * @param parentId id of the parent change
     * @param withParentBlock code to be executed with given parent change
     * @return the value returned from the withDefaultAuthor
     */
    static <R> R withParentId(Long parentId, Closure<R> withParentBlock) {
        SessionFactory sessionFactory = Holders.applicationContext.sessionFactory
        Auditor auditor = auditor.get()
        Long currentParent = auditor.parentChangeId
        auditor.parentChangeId = parentId
        sessionFactory.currentSession?.flush()
        R result = withParentBlock()
        sessionFactory.currentSession?.flush()
        auditor.parentChangeId = currentParent
        return result
    }

    ListWithTotalAndType<Change> getChangesForUser(Map params, User user) {
        long authorId = user.id
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }
        Lists.fromCriteria(params, Change) {
            eq 'authorId', authorId
            ne 'otherSide', Boolean.TRUE
            ne 'system', Boolean.TRUE
        }
    }

    ListWithTotalAndType<Change> getChanges(Map params, CatalogueElement element) {
        long latestId = element.latestVersionId ?: element.id
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }
        Lists.fromCriteria(params, Change) {
            eq 'latestVersionId', latestId
            ne 'system', Boolean.TRUE
        }
    }

    ListWithTotalAndType<Change> getSubChanges(Map params, Change change) {
        if (!change) {
            return Lists.emptyListWithTotalAndType(Change)
        }

        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }

        Lists.fromCriteria(params, Change) {
            eq 'parentId', change.id
            ne 'system', Boolean.TRUE
            ne 'otherSide', Boolean.TRUE
        }
    }

    CatalogueElement logNewVersionCreated(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        Long changeId = auditor.get().logNewVersionCreated(element, modelCatalogueSecurityService.currentUser?.id)
        CatalogueElement ce = withParentId(changeId, createDraftBlock)
        if (!ce) {
            return ce
        }
        if (changeId) {
            Change change = Change.get(changeId)
            change.changedId = ce.id
            FriendlyErrors.failFriendlySave(change)
        }
        ce
    }

    CatalogueElement logElementFinalized(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        withParentId(auditor.get().logElementFinalized(element, modelCatalogueSecurityService.currentUser?.id), createDraftBlock)
    }


    CatalogueElement logElementDeprecated(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        withParentId(auditor.get().logElementDeprecated(element, modelCatalogueSecurityService.currentUser?.id), createDraftBlock)
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

    void logElementCreated(CatalogueElement element) {
        auditor.get().logElementCreated(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logElementDeleted(CatalogueElement element) {
        auditor.get().logElementDeleted(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logElementUpdated(CatalogueElement element) {
        auditor.get().logElementUpdated(element, modelCatalogueSecurityService.currentUser?.id)
    }

    void logMappingCreated(Mapping mapping) {
        auditor.get().logMappingCreated(mapping, modelCatalogueSecurityService.currentUser?.id)
    }

    void logMappingDeleted(Mapping mapping) {
        auditor.get().logMappingDeleted(mapping, modelCatalogueSecurityService.currentUser?.id)
    }

    void logMappingUpdated(Mapping mapping) {
        auditor.get().logMappingUpdated(mapping, modelCatalogueSecurityService.currentUser?.id)
    }

    void logNewRelation(Relationship relationship) {
        auditor.get().logNewRelation(relationship, modelCatalogueSecurityService.currentUser?.id)
    }

    void logRelationRemoved(Relationship relationship) {
        auditor.get().logRelationRemoved(relationship, modelCatalogueSecurityService.currentUser?.id)
    }

    void logRelationArchived(Relationship relationship) {
        auditor.get().logRelationArchived(relationship, modelCatalogueSecurityService.currentUser?.id)
    }


}
