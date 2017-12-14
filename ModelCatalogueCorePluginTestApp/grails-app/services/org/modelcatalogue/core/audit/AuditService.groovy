package org.modelcatalogue.core.audit

import grails.gorm.DetachedCriteria
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.messaging.core.MessageSendingOperations
import javax.annotation.PostConstruct
import java.util.concurrent.Callable

/**
 * AuditService manages everything related to auditing in Metadata Exchange.
 *
 * This means that every creating, update or deletion of catalogue element, relationship or metadata of one of these
 * is recorded. The AuditService delegates most of the notification methods to implementations of Auditor interface
 * which for example records the changes into the database or sends notifications to the front end.
 */
class AuditService {

    static transactional = false

    SearchCatalogue modelCatalogueSearchService
    def modelCatalogueSecurityService
    def dataModelService
    def executorService
    def sessionFactory
    MessageSendingOperations brokerMessagingTemplate
    SpringSecurityService springSecurityService

    static Callable<Auditor> auditorFactory = { throw new IllegalStateException("Application is not initialized yet") }

    private static ThreadLocal<Auditor> auditor = new ThreadLocal<Auditor>() {
        protected Auditor initialValue() {
            return auditorFactory()
        }
    }


   @PostConstruct
   void hookSearchService() {
       auditorFactory =  { return CompoundAuditor.from(new DefaultAuditor(executorService), new EventNotifier(brokerMessagingTemplate, executorService)) }
       if (modelCatalogueSearchService.indexingManually) {
        Callable<Auditor> oldFactory = auditorFactory
        auditorFactory = { CompoundAuditor.from(oldFactory(), new SearchNotifier(modelCatalogueSearchService))}
       }
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

    ListWithTotalAndType<Change> getGlobalChanges(Map params, DataModelFilter dataModels) {
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }

        if (!dataModels) {
            return Lists.fromCriteria(params, Change) {
                isNull 'parentId'
                ne 'system', true
                ne 'otherSide', true

            }
        }


        Map<String, Object> args = [:]
        String subquery = getClassifiedElementsSubQuery(dataModels, args)

        //language=HQL
        Lists.fromQuery params, Change, """
            from Change c
            where c.parentId is null and c.system != true and c.otherSide != true and c.changedId  in (""" + subquery + """)""", args
    }

    private static String getClassifiedElementsSubQuery(DataModelFilter dataModels, Map<String, Object> args) {
        if (dataModels.unclassifiedOnly) {
            // language=HQL
            return """
                select ce.id
                from CatalogueElement ce
                where ce.dataModel is null
            """
        }
        if (dataModels.excludes && !dataModels.includes) {
            args.excludes = dataModels.excludes
            // language=HQL
            return """
                select ce
                from CatalogueElement ce
                where
                    ce.dataModel.id not in (:excludes) or ce.id not in (:excludes)
            """
        }
        if (dataModels.excludes && dataModels.includes) {
            throw new IllegalStateException("Combining exclusion and inclusion is no longer supported. Exclusion would be ignored!")
        }
        if (dataModels.includes && !dataModels.excludes) {
            args.includes = dataModels.includes
            // language=HQL
            return """
                select ce from CatalogueElement ce
                where ce.dataModel.id in (:includes) or ce.id in (:includes)
            """
        }
        throw new IllegalArgumentException("Data model filter must be set")
    }

    ListWithTotalAndType<Change> getChanges(Map params, CatalogueElement element, @DelegatesTo(DetachedCriteria) Closure additionalCriteria = {}) {
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }

        DetachedCriteria<Change> criteria = new DetachedCriteria<Change>(Change).build {
            eq 'changedId', element.id
            ne 'system', Boolean.TRUE
        }.build additionalCriteria



        Lists.fromCriteria(params, criteria)
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

    /**
     * Returns pageable list of changes which only applies for given element's versions.
     * @param params incoming parameters such as page size and offset
     * @param latestVersionId id of the very first version of the element
     * @return pageable list of changes which only applies for given element's versions
     */
    ListWithTotalAndType<Change> getElementChanges(Map params, Long latestVersionId){
        if (!params.sort) {
            params.sort  = 'dateCreated'
            params.order = 'desc'
        }

        // closure is called in two roles - as list query factory (in that case it's called with argument of type Map)
        // or as count query factory (in that case it's called without any argument)
        Closure queryClosure = {

            def max = it?.max
            def offset = it?.offset

            // is true if we are searching for items themself not just their count
            boolean itemsQuery = it instanceof Map

            final session = sessionFactory.currentSession

            // change is reserved word in SQL so it needs to be escaped properly
            String tableName = 'change'

            if (sessionFactory.currentSession.connection().metaData.databaseProductName == 'MySQL') {
                // MySQL uses backticks to escape the keywords
                tableName = "`change`"
            } else if (sessionFactory.currentSession.connection().metaData.databaseProductName == 'H2') {
                // H2 uses double quotes to escape the keywords
                tableName = '"change"'
            } else {
                // if we cannot determine the database we use backtick which is used more often
                log.warn "Cannot quote the change table name properly, using backticks."
                tableName = "`$tableName`"
            }

            // We are looking for all the changes which has the latest version id logged as the one passed into this
            // method and which are not system or mirroring changes (e.g. they are destinations of relationships changed).
            // We are only interested in the changes which has no parents (they are created directly by users) or they
            // have been created as part of external change (import).
            String query = """
                select ch.* from $tableName ch
                left join $tableName parent on parent.id = ch.parent_id
                where ch.latest_version_id = :lvid
                and ch.system <> true
                and ch.other_side <> true
                and (ch.parent_id is null or parent.type = 'EXTERNAL_UPDATE')
                order by ch.date_created DESC 

            """

            // Create native SQL query.
            final sqlQuery = session.createSQLQuery(query)

            // Use Groovy with() method to invoke multiple methods
            // on the sqlQuery object.
            final results = sqlQuery.with {
                // Set domain class as entity.
                // Properties in domain class id, name, level will
                // be automatically filled.
                addEntity(Change)

                // Set value for parameter startId.
                setLong('lvid', latestVersionId)

                if (max) {
                    setMaxResults(max as Integer)
                }

                if (offset) {
                    setFirstResult(offset as Integer)
                }

                // Get all results.
                if (itemsQuery) {
                    return list()
                }
                return list().size()
            }

            results
        }

        Lists.lazy(params, Change, queryClosure, queryClosure)
    }

    CatalogueElement logNewVersionCreated(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        Long changeId = auditor.get().logNewVersionCreated(element, loggedUserId()).toBlocking().first()
        CatalogueElement ce = withParentId(changeId, createDraftBlock)
        if (!ce) {
            return ce
        }
        if (changeId) {
            Change change = Change.get(changeId)
            if (!change) {
                Change.withSession {
                    it.flush()
                }
                change = Change.get(changeId)
                if (!change) {
                    log.error("Cannot bind changedId to Change[$changeId] - not found")
                    return ce
                }
            }
            change.changedId = ce.id
            change.dateCreated = new Date()
            FriendlyErrors.failFriendlySave(change)
        }
        ce
    }

    CatalogueElement logElementFinalized(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        withParentId(auditor.get().logElementFinalized(element, loggedUserId()).toBlocking().first(), createDraftBlock)
    }


    CatalogueElement logElementDeprecated(CatalogueElement element, Closure<CatalogueElement> createDraftBlock) {
        withParentId(auditor.get().logElementDeprecated(element, loggedUserId()).toBlocking().first(), createDraftBlock)
    }

    CatalogueElement logExternalChange(CatalogueElement source, Long authorId, String message, Closure<CatalogueElement> createDraftBlock) {
        withDefaultAuthorAndParentAction(authorId ,auditor.get().logExternalChange(source, message, loggedUserId()).toBlocking().first(), createDraftBlock)
    }


    void logNewMetadata(ExtensionValue extension) {
        auditor.get().logNewMetadata(extension, loggedUserId())
    }

    void logMetadataUpdated(ExtensionValue extension) {
        auditor.get().logMetadataUpdated(extension, loggedUserId())
    }

    void logMetadataDeleted(ExtensionValue extension) {
        auditor.get().logMetadataDeleted(extension, loggedUserId())
    }

    void logNewRelationshipMetadata(RelationshipMetadata extension) {
        auditor.get().logNewRelationshipMetadata(extension, loggedUserId())
    }

    void logRelationshipMetadataUpdated(RelationshipMetadata extension) {
        auditor.get().logRelationshipMetadataUpdated(extension, loggedUserId())
    }

    void logRelationshipMetadataDeleted(RelationshipMetadata extension) {
        auditor.get().logRelationshipMetadataDeleted(extension, loggedUserId())
    }

    void logElementCreated(CatalogueElement element) {
        auditor.get().logElementCreated(element, loggedUserId())
    }

    void logElementDeleted(CatalogueElement element) {
        auditor.get().logElementDeleted(element, loggedUserId())
    }

    void logElementUpdated(CatalogueElement element) {
        auditor.get().logElementUpdated(element, loggedUserId())
    }

    void logMappingCreated(Mapping mapping) {
        auditor.get().logMappingCreated(mapping, loggedUserId())
    }

    void logMappingDeleted(Mapping mapping) {
        auditor.get().logMappingDeleted(mapping, loggedUserId())
    }

    void logMappingUpdated(Mapping mapping) {
        auditor.get().logMappingUpdated(mapping, loggedUserId())
    }

    void logNewRelation(Relationship relationship) {
        auditor.get().logNewRelation(relationship, loggedUserId())
    }

    void logRelationRemoved(Relationship relationship) {
        auditor.get().logRelationRemoved(relationship, loggedUserId())
    }

    void logRelationArchived(Relationship relationship) {
        auditor.get().logRelationArchived(relationship, loggedUserId())
    }

    Long loggedUserId() {
        if ( springSecurityService.principal instanceof String ) {
            try {
                return springSecurityService.principal as Long
            } catch(NumberFormatException e) {
                return null
            }
        }
        if ( springSecurityService.principal.respondsTo('id') ) {
            return springSecurityService.principal.id as Long
        }
        null
    }
}
