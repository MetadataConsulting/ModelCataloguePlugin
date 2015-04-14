package org.modelcatalogue.core.audit

import grails.util.Holders
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

class AuditService {

    static transactional = false

    def modelCatalogueSecurityService
    def classificationService

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
    public <R> R mute(Closure<R> noAuditBlock) {
        SessionFactory sessionFactory = Holders.applicationContext.sessionFactory
        Auditor auditor = auditor.get()
        Boolean currentSystem = auditor.system
        auditor.system = true
        sessionFactory.currentSession?.flush()
        R result = noAuditBlock()
        sessionFactory.currentSession?.flush()
        auditor.system = currentSystem
        return result
    }


    /**
     * Allows to run block as given author if no author is available. This is supposed to be used to set the author
     * for actions happening in separate thread.
     * @param defaultAuthorId default author id to be used if no author is known
     * @param withDefaultAuthorBlock code to be executed with default author
     * @return the value returned from the withDefaultAuthor
     */
    public <R> R withDefaultAuthorId(Long defaultAuthorId, Closure<R> withDefaultAuthorBlock) {
        SessionFactory sessionFactory = Holders.applicationContext.sessionFactory
        Auditor auditor = auditor.get()
        Long currentDefault = auditor.defaultAuthorId
        auditor.defaultAuthorId = defaultAuthorId
        sessionFactory.currentSession?.flush()
        R result = withDefaultAuthorBlock()
        sessionFactory.currentSession?.flush()
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
    public <R> R withParentId(Long parentId, Closure<R> withParentBlock) {
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

    public <R> R withDefaultAuthorAndParentAction(Long defaultAuthorId, Long parentId, Closure<R> withDefaultAuthorBlock) {
        SessionFactory sessionFactory = Holders.applicationContext.sessionFactory
        Auditor auditor = auditor.get()

        Long currentDefault = auditor.defaultAuthorId
        auditor.defaultAuthorId = defaultAuthorId

        Long currentParent = auditor.parentChangeId
        auditor.parentChangeId = parentId

        sessionFactory.currentSession?.flush()

        R result = withDefaultAuthorBlock()

        sessionFactory.currentSession?.flush()

        auditor.defaultAuthorId = currentDefault
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

    ListWithTotalAndType<Change> getGlobalChanges(Map params, ClassificationFilter classifications) {
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }

        if (!classifications) {
            return Lists.all(params, Change)
        }


        Map<String, Object> args = [:]
        String subquery = getClassifiedElementsSubQuery(classifications, args)

        //language=HQL
        Lists.fromQuery params, Change, """
            from Change c
            where c.changedId in (""" + subquery + """)""", args
    }

    private static String getClassifiedElementsSubQuery(ClassificationFilter classifications, Map<String, Object> args) {
        args.classificationType = RelationshipType.classificationType
        if (classifications.unclassifiedOnly) {
            // language=HQL
            return """
                select ce.id
                from CatalogueElement ce
                where ce not in (select r.destination from Relationship r where r.relationshipType = :classificationType)
            """
        }
        if (classifications.excludes && !classifications.includes) {
            args.excludes = classifications.excludes
            // language=HQL
            return """
                select ce
                from CatalogueElement ce
                where ce.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
            """
        }
        if (classifications.excludes && classifications.includes) {
            args.excludes = classifications.excludes
            args.includes = classifications.includes
            // language=HQL
            return """
                select ce
                from CatalogueElement ce
                where ce.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
                  and ce.id     in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes))
            """
        }
        if (classifications.includes && !classifications.excludes) {
            args.includes = classifications.includes
            // language=HQL
            return """
                select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes)
            """
        }
        throw new IllegalArgumentException("Classification fitler must be set")
    }

    ListWithTotalAndType<Change> getChanges(Map params, CatalogueElement element) {
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }
        Lists.fromCriteria(params, Change) {
            eq 'changedId', element.id
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
            change.dateCreated = new Date()
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

    CatalogueElement logExternalChange(CatalogueElement source, Long authorId, String message, Closure<CatalogueElement> createDraftBlock) {
        withDefaultAuthorAndParentAction(authorId ,auditor.get().logExternalChange(source, message, modelCatalogueSecurityService.currentUser?.id), createDraftBlock)
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
