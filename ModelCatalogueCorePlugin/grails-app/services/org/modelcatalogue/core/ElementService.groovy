package org.modelcatalogue.core

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.transaction.TransactionStatus

class ElementService implements Publisher<CatalogueElement> {

    private static final Cache<Long, Integer> VERSION_COUNT_CACHE = CacheBuilder.newBuilder().initialCapacity(1000).build()
    private static Cache<Class, List<Class>> subclassesCache = CacheBuilder.newBuilder().initialCapacity(20).build()

    static transactional = false

    def grailsApplication
    def relationshipService
    def modelCatalogueSearchService
    def messageSource
    def auditService

    List<CatalogueElement> list(Map params = [:]) {
        CatalogueElement.findAllByStatusInList(getStatusFromParams(params), params)
    }

    public <E extends CatalogueElement> List<E> list(params = [:], Class<E> resource) {
        resource.findAllByStatusInList(getStatusFromParams(params), params)
    }

    Long count(params = [:]) {
        CatalogueElement.countByStatusInList(getStatusFromParams(params))
    }

    public <E extends CatalogueElement> Long count(params = [:], Class<E> resource) {
        resource.countByStatusInList(getStatusFromParams(params))
    }



    public DataModel createDraftVersion(DataModel dataModel, String newSemanticVersion, DraftContext context) {
        dataModel.checkNewSemanticVersion(newSemanticVersion)

        if (dataModel.hasErrors()) {
            return dataModel
        }

        context.version(newSemanticVersion)

        Closure<DataModel> code = { TransactionStatus status = null ->
            return (DataModel) auditService.logNewVersionCreated(dataModel) {
                DataModel draft = (DataModel) dataModel.createDraftVersion(this, context)
                if (draft.hasErrors()) {
                    status?.setRollbackOnly()
                    return dataModel
                }
                context.resolvePendingRelationships()

                // TODO: better target the changes
                VERSION_COUNT_CACHE.invalidateAll()

                return draft
            }
        }
        if (context.importFriendly) {
            return code()
        } else {
            return (DataModel) CatalogueElement.withTransaction(code)
        }
    }

    /**
     * @deprecated use #createDraftVersion(DatModel, String, DraftContext) instead
     */

    public <E extends CatalogueElement> E createDraftVersion(E element, DraftContext context) {
        Closure<E> code = { TransactionStatus status = null ->
            return (E) auditService.logNewVersionCreated(element) {
                E draft = element.createDraftVersion(this, context) as E
                if (draft.hasErrors()) {
                    status?.setRollbackOnly()
                    return element
                }
                context.resolvePendingRelationships()

                // TODO: better target the changes
                VERSION_COUNT_CACHE.invalidateAll()

                return draft
            }
        }
        if (context.importFriendly) {
            return code()
        } else {
            return (E) CatalogueElement.withTransaction(code)
        }
    }

    public <E extends CatalogueElement> E cloneElement(E element, CloningContext context) {
        return (E) CatalogueElement.withTransaction { TransactionStatus status = null ->
            return (E) auditService.logNewVersionCreated(element) {
                E draft = element.cloneElement(this, context) as E
                if (draft.hasErrors()) {
                    status?.setRollbackOnly()
                    return element
                }
                context.resolvePendingRelationships()
                return draft
            }
        }
    }



    CatalogueElement archive(CatalogueElement archived, boolean archiveRelationships) {
        if (archived.archived) {
            return archived
        }

        CatalogueElement.withTransaction { TransactionStatus status ->
            auditService.logElementDeprecated(archived) {
                if (archiveRelationships) {
                    archived.incomingRelationships.each {
                        if (it.relationshipType == RelationshipType.supersessionType) {
                            return
                        }
                        it.archived = true
                        FriendlyErrors.failFriendlySave(it)
                        auditService.logRelationArchived(it)
                    }

                    archived.outgoingRelationships.each {
                        if (it.relationshipType == RelationshipType.supersessionType) {
                            return
                        }
                        it.archived = true
                        FriendlyErrors.failFriendlySave(it)
                        auditService.logRelationArchived(it)
                    }
                }

                modelCatalogueSearchService.unindex(archived)

                archived.status = ElementStatus.DEPRECATED
                archived.save(flush: true, validate: false)
                return archived
            }
        }
    }


    public DataModel finalizeDataModel(DataModel draft, String version, String revisionNotes) {
        draft.checkPublishSemanticVersion(version)

        if (!revisionNotes) {
            draft.errors.rejectValue('revisionNotes', 'finalize.revisionNotes.null', 'Please, provide the revision notes')
        }

        if (draft.hasErrors()) {
            return draft
        }
        return (DataModel) CatalogueElement.withTransaction { TransactionStatus status ->
            auditService.logElementFinalized(draft) {
                DataModel finalized = draft.publish(this) as DataModel

                if (finalized.hasErrors()) {
                    status.setRollbackOnly()
                }

                finalized.semanticVersion = version
                finalized.revisionNotes = revisionNotes

                finalized
            }
        }
    }

    public <E extends CatalogueElement> E finalizeElement(E draft) {
        return (E) CatalogueElement.withTransaction { TransactionStatus status ->
            auditService.logElementFinalized(draft) {
                E finalized = draft.publish(this) as E
                if (finalized.hasErrors()) {
                    status.setRollbackOnly()
                }
                finalized
            }
        }
    }

    static List<ElementStatus> getStatusFromParams(params) {
        if (!params.status) {
            return ElementStatus.values().toList()
        } else if (params.status == 'active') {
            return [ElementStatus.FINALIZED, ElementStatus.DRAFT]
        }
        if (params.status instanceof ElementStatus) {
            return [params.status as ElementStatus]
        }
        return [ElementStatus.valueOf(params.status.toString().toUpperCase())]
    }


    public <E extends CatalogueElement> E merge(E source, E destination, DataModel dataModel = source.dataModel) {
        log.info "Merging $source into $destination"
        if (destination == null) return null

        if (source == null) {
            destination.errors.reject('merge.source.missing', 'Source is missing')
            return destination
        }

        // fallthrough if already merge in progress or the source and destination are the same
        if (source == destination || destination.status == ElementStatus.UPDATED || source.status == ElementStatus.UPDATED) {
            return destination
        }


        // do not merge with already archived ones
        if (destination.status != ElementStatus.DRAFT) {
            destination.errors.reject('merge.destination.no.draft', 'Destination is not draft')
            return destination
        }

        // do not merge with already archived ones
        if (source.status != ElementStatus.DRAFT) {
            destination.errors.reject('merge.source.no.draft', 'Source is not draft')
            return destination
        }

        if (destination.class != source.class && !(destination.instanceOf(DataType) && source.instanceOf(DataType))) {
            destination.errors.reject('merge.not.same.type', 'Both source and destination must be of the same type')
            return destination
        }

        ElementStatus originalStatus = destination.status

        GrailsDomainClass grailsDomainClass = grailsApplication.getDomainClass(source.class.name) as GrailsDomainClass

        for (GrailsDomainClassProperty property in grailsDomainClass.persistentProperties) {
            if (property.manyToOne || property.oneToOne) {
                def dstProperty = destination.getProperty(property.name)
                def srcProperty = source.getProperty(property.name)

                if (dstProperty && srcProperty && dstProperty != dstProperty) {
                    destination.errors.rejectValue property.name, 'merge.both.set.' + property.name, "Property '$property.name' is set in both source and destination. Delete it prior the merge."
                    return destination
                }

                destination.setProperty(property.name, dstProperty ?: srcProperty)
            }
        }

        destination.validate()

        if (destination.hasErrors()) {
            return destination
        }

        destination.status = ElementStatus.UPDATED

        if (!destination.dataModel) {
            destination.dataModel = source.dataModel
        }

        destination.save(flush: true, validate: false)


        for (Map.Entry<String, String> extension in new HashMap<String, String>(source.ext)) {
            if(!destination.ext.containsKey(extension.key)) {
                destination.ext.put(extension.key, extension.value)
            }
        }

        for (Relationship rel in new HashSet<Relationship>(source.outgoingRelationships)) {
            if (rel.relationshipType.system) {
                // skip system, currently only supersession
                continue
            }

            if (rel.destination == destination) {
                // do not self-reference
                continue
            }

            if (rel.archived || rel.destination.archived) {
                // no need to transfer archived elements
                continue
            }

            Relationship existing = destination.outgoingRelationships.find { it.destination.name == rel.destination.name && it.relationshipType == rel.relationshipType }

            if (existing) {
                if (rel.destination instanceof CatalogueElement && existing.destination instanceof CatalogueElement && rel.destination.class == existing.destination.class && existing.destination != destination) {
                    if (rel.destination.dataModel == dataModel) {
                        merge rel.destination, existing.destination, dataModel
                    }
                }
                continue
            }

            Relationship newOne = relationshipService.link destination, rel.destination, rel.relationshipType, rel.archived
            if (newOne.hasErrors()) {
                destination.errors.rejectValue 'outgoingRelationships', 'unable.to.transfer.relationships', [rel, newOne.errors.allErrors.collect { messageSource.getMessage(it, Locale.default)}.join(", ")] as Object[],  "Unable to transfer relationship {0}. Errors: {1}"

            } else {
                newOne.ext.putAll(rel.ext)
            }
        }

        for (Relationship rel in new HashSet<Relationship>(source.incomingRelationships)) {
            if (rel.relationshipType.system) {
                continue
            }

            if (rel.source == destination) {
                // do not self-reference
                continue
            }

            if (rel.archived || rel.source.archived) {
                // no need to transfer archived elements
                continue
            }

            Relationship existing = destination.incomingRelationships.find { it.source.name == rel.source.name && it.relationshipType == rel.relationshipType }

            if (existing) {
                if (rel.source.class == existing.source.class && existing.source != destination) {
                    if (rel.source.dataModel == dataModel) {
                        merge rel.source, existing.source, dataModel
                    }
                }
                continue
            }

            Relationship newOne = relationshipService.link rel.source, destination, rel.relationshipType, rel.archived
            if (newOne.hasErrors()) {
                destination.errors.rejectValue 'incomingRelationships', 'unable.to.transfer.relationships', [rel, newOne.errors.allErrors.collect { messageSource.getMessage(it, Locale.default)}.join(", ")] as Object[],  "Unable to transfer relationship {0}. Errors: {1}"
            } else {
                newOne.ext.putAll(rel.ext)
            }
        }

        if (destination.hasErrors()) {
            return destination
        }

        if (!source.archived) {
            archive source, true
        }

        destination.addToSupersededBy(source, skipUniqueChecking: true)

        destination.status = originalStatus

        FriendlyErrors.failFriendlySave(destination)

        source.afterMerge(destination)

        log.info "Merged $source into $destination"

        destination
    }


    Map<Long, Long> findModelsToBeInlined() {
        if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST]) {
            // does not work with H2 database
            log.warn "Trying to find inlined models in development mode. This feature does not work with H2 database"
            return [:]
        }
        List<DataClass> models = DataClass.executeQuery("""
            select m
            from DataClass m left join m.incomingRelationships inc
            group by m.name
            having sum(case when inc.relationshipType = :base then 1 else 0 end) = 1
            and sum(case when inc.relationshipType = :hierarchy then 1 else 0 end) = 2
        """, [hierarchy: RelationshipType.hierarchyType, base: RelationshipType.readByName("base")])

        Map<Long, Long> ret = [:]

        for (DataClass model in models) {
            if (model.ext.from == 'xs:element') {
                ret[model.id] = model.isBasedOn[0].id
            }
        }

        ret
    }

    /**
     * Return models which are very likely to be duplicates.
     * For models having same name as at least one other Model check if they contains same child models and data
     * elements. If so return their id and the set of ids of similar models.
     * @return map with the model id as key and set of ids of duplicate models as value
     */
    Map<Long, Set<Long>> findDuplicateModelsSuggestions() {
        // TODO: create test
        Object[][] results = DataClass.executeQuery """
            select m.id, m.name, rel.relationshipType.name,  rel.destination.name
            from DataClass m join m.outgoingRelationships as rel
            where
                m.name in (
                    select model.name from DataClass model
                    where model.status in :states
                    group by model.name
                    having count(model.id) > 1
                )
            and
                m.status in :states
            and
                rel.archived = false
            and
                (rel.relationshipType = :containment or rel.relationshipType = :hierarchy)
            order by m.name asc, m.dateCreated asc, rel.destination.name asc
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], containment: RelationshipType.readByName('containment'), hierarchy: RelationshipType.readByName('hierarchy')]


        Map<Long, Map<String, Object>> models = new LinkedHashMap<Long, Map<String, Object>>().withDefault { [id: it, elementNames: new TreeSet<String>(), childrenNames: new TreeSet<String>()] }

        for (Object[] row in results) {
            def info = models[row[0] as Long]
            info.name = row[1]
            if (row[2] == 'containment') {
                info.elementNames << row[3].toString()
                info.elementNamesSize = info.elementNames.size()
            } else {
                info.childrenNames << row[3].toString()
                info.childrenNamesSize = info.childrenNames.size()
            }
        }

        Map<Long, Set<Long>> suggestions = new LinkedHashMap<Long, Set<Long>>().withDefault { new TreeSet<Long>() }


        def current = null

        models.each { id, info ->
            if (!current) {
                current = info
            } else {
                if (info.name == current.name && info.elementNamesSize == current.elementNamesSize && info.childrenNamesSize == current.childrenNamesSize && info.elementNames == current.elementNames && info.childrenNames == current.childrenNames) {
                    suggestions[current.id] << info.id
                } else {
                    current = info
                }
            }
        }

        suggestions
    }

    /**
     * Return enumerated types which are very likely to be duplicates.
     * Enums are very likely duplicates if they have similar enum values.
     * @return map with the enum id as key and set of ids of duplicate enums as value
     */
    Map<Long, Set<Long>> findDuplicateEnumerationsSuggestions() {
        Object[][] results = EnumeratedType.executeQuery """
            select e.id, e.enumAsString
            from EnumeratedType e
            where e.status in :states
            order by e.name
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED]]


        Map<String, Set<Long>> enums = [:].withDefault { [] as TreeSet<Long> }

        for (Object[] row in results) {
            enums[getNormalizedEnumValues(EnumeratedType.stringToMap(row[1]))] << row[0]
        }

        enums.findAll { String key, Set<Long> values -> values.size() > 1 }.collectEntries { String key, Set<Long> values ->
            def valuesAsList = values.asList()
            [valuesAsList.first(), valuesAsList[1..-1]]
        }

    }

    String getNormalizedEnumValues(Map<String, String> enumValues) {
        enumValues.keySet().collect {
            if (it ==~ /\d+/) {
                return "" + Long.parseLong(it, 10)
            }
            return it
        }.sort().join(':')
    }


    public <E extends CatalogueElement> E restore(E element) {
        if (!element) {
            return element
        }

        if (!element) {
            element.errors.rejectValue('status', 'element.restore.not.deprecated', 'Unable to restore element. Element is not deprecated.')
            return element
        }

        if (element.latestVersionId) {
            CatalogueElement finalized = CatalogueElement.findByLatestVersionIdAndStatus(element.latestVersionId, ElementStatus.FINALIZED)
            if (finalized) {
                element.errors.rejectValue('status', 'element.restore.finalized.exists', 'Unable to restore element. There is already a finalized version of this element.')
                return element
            }
        }

        element.status = ElementStatus.FINALIZED
        element.save(flush: true)
        return element
    }

    int countVersions(CatalogueElement catalogueElement) {
        Long id = catalogueElement.getId()

        if (!id) {
            return 1
        }

        VERSION_COUNT_CACHE.get(id) {
            if (!catalogueElement.getLatestVersionId()) {
                return 1
            }
            CatalogueElement.countByLatestVersionId(catalogueElement.getLatestVersionId())
        }

    }

    static void clearCache() {
        VERSION_COUNT_CACHE.invalidateAll()
        VERSION_COUNT_CACHE.cleanUp()
    }


    List<Class> collectSubclasses(Class<?> resource) {
        subclassesCache.get(resource) {
            GrailsDomainClass domainClass = grailsApplication.getDomainClass(resource.name) as GrailsDomainClass

            if (domainClass.hasSubClasses()) {
                return [resource] + domainClass.subClasses.collect { it.clazz }
            }

            return [resource]
        }
    }

}
