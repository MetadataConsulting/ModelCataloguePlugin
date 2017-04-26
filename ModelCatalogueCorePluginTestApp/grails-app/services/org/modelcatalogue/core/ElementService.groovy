package org.modelcatalogue.core

import com.google.common.collect.ImmutableList
import grails.gorm.DetachedCriteria
import grails.util.Environment
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftChain
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.Legacy
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.transaction.TransactionStatus
import rx.Observer


/** Used in draft/finalization chain. */
class ElementService implements Publisher<CatalogueElement> {


    static transactional = false

    GrailsApplication grailsApplication
    RelationshipService relationshipService
    SearchCatalogue modelCatalogueSearchService
    SecurityService modelCatalogueSecurityService
    def messageSource
    AuditService auditService

    List<CatalogueElement> list(Map params = [:]) {
        CatalogueElement.findAllByStatusInList(getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER')), params)
    }

    public <E extends CatalogueElement> List<E> list(params = [:], Class<E> resource) {
        resource.findAllByStatusInList(getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER')), params)
    }

    Long count(params = [:]) {
        CatalogueElement.countByStatusInList(getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER')))
    }

    public <E extends CatalogueElement> Long count(params = [:], Class<E> resource) {
        resource.countByStatusInList(getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER')))
    }

    public DataModel createDraftVersion(DataModel dataModel, String newSemanticVersion, DraftContext context) {
        dataModel.checkNewSemanticVersion(newSemanticVersion)

        if (dataModel.hasErrors()) {
            context.monitor.onError(new IllegalArgumentException(FriendlyErrors.printErrors("Wrong semantic version", dataModel.errors)))
            return dataModel
        }

        context.version(newSemanticVersion)

        Closure<DataModel> code = { TransactionStatus status = null ->
            return (DataModel) auditService.logNewVersionCreated(dataModel) {
                DataModel draft = PublishingChain.createDraft(dataModel, context.within(dataModel)).run(this, context.monitor) as DataModel
                if (draft.hasErrors()) {
                    status?.setRollbackOnly()
                    return dataModel
                }

                // TODO: better target the changes
                CacheService.VERSION_COUNT_CACHE.invalidateAll()

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
        if (!element) {
            return element
        }

        if (element.instanceOf(DataModel)) {
            DataModel dataModel = element as DataModel
            return createDraftVersion(dataModel, PublishingContext.nextPatchVersion(dataModel.semanticVersion), context) as E
        }

        if (!element.dataModel) {
            log.warn "draft requested for $element but it does not have any Data Model assigned. New Data Model will be created and assigned"
            DataModel dataModel = new DataModel(status: ElementStatus.FINALIZED, name: "$element.name Data Model", description: "This data model was automatically created for ${GrailsNameUtils.getNaturalName(element.getClass().simpleName)} $element.name when creating draft was requested on that element ${GrailsNameUtils.getNaturalName(element.getClass().simpleName)}.")
            FriendlyErrors.failFriendlySave(dataModel)
            element.dataModel = dataModel
            FriendlyErrors.failFriendlySave(element)
            createDraftVersion(dataModel, PublishingContext.nextPatchVersion(dataModel.semanticVersion), context)
            return context.resolve(element) as E
        }

        DataModel draftDataModel = createDraftVersion(element.dataModel, context.version ?: PublishingContext.nextPatchVersion(element.dataModel.semanticVersion), context)

        E draft = context.resolve(element) as E

        if (!draft) {
            throw new IllegalStateException("Data model $draftDataModel created without the draft version of $element")
        }

        return draft
    }

    public <E extends CatalogueElement> E cloneElement(E element, CloningContext context) {
        return (E) CatalogueElement.withTransaction { TransactionStatus status = null ->
            return (E) auditService.logNewVersionCreated(element) {
                E draft = element.cloneElement(this, context) as E
                if (draft.hasErrors()) {
                    status?.setRollbackOnly()
                    return element
                }
                context.resolvePendingRelationships(context.monitor)
                return draft
            }
        }
    }

    CatalogueElement findByModelCatalogueId(Class<? extends CatalogueElement> resource, String theId, Long maxCatalogueElementId = Long.MAX_VALUE) {
        if (!theId) {
            return null
        }

        CatalogueElement byExternalId = getLatestFromCriteria(new DetachedCriteria<CatalogueElement>(resource).build {
            eq 'modelCatalogueId', theId
        })

        if (byExternalId) {
            return byExternalId
        }

        def matchNewScheme = theId.toString() =~ /\/(.\w+)\/(\d+)(@(.+))?$/

        if (matchNewScheme) {
            Long urlId = matchNewScheme[0][2] as Long
            String version = matchNewScheme[0][4] as String
            Long versionNumberFound = null

            def matchVersionNumber = version =~ /0\.0\.(\d+)/

            if (matchVersionNumber) {
                versionNumberFound = matchVersionNumber[0][1] as Long
            }

            if (urlId > maxCatalogueElementId) {
                return null
            }

            CatalogueElement result = getLatestFromCriteria(new DetachedCriteria<CatalogueElement>(resource).build {
                or {
                    eq 'latestVersionId', urlId
                    eq 'id', urlId
                }
                if (version) {
                    dataModel {
                        if (versionNumberFound) {
                            or {
                                eq 'semanticVersion', version
                                eq 'versionNumber', versionNumberFound
                            }
                        } else {
                            eq 'semanticVersion', version
                        }
                    }
                }
            })

            if (result && result.getDefaultModelCatalogueId(version == null).contains(Legacy.fixModelCatalogueId(theId).toString())) {
                return result
            }

            if (resource == DataModel || resource == CatalogueElement && HibernateHelper.getEntityClass(CatalogueElement.findByLatestVersionId(urlId)) == DataModel) {
                result = getLatestFromCriteria(new DetachedCriteria<CatalogueElement>(resource).build {
                    or {
                        eq 'latestVersionId', urlId
                        eq 'id', urlId
                    }
                    if (version) {
                        if (versionNumberFound) {
                            or {
                                eq 'semanticVersion', version
                                eq 'versionNumber', versionNumberFound
                            }
                        } else {
                            eq 'semanticVersion', version
                        }
                    }
                })
            }


            if (result && result.getDefaultModelCatalogueId(version == null).contains(Legacy.fixModelCatalogueId(theId).toString())) {
                return result
            }

            if (versionNumberFound) {
                CatalogueElement byVersionNumber = getLatestFromCriteria(new DetachedCriteria<CatalogueElement>(resource).build {
                    or {
                        eq 'latestVersionId', urlId
                        eq 'id', urlId
                    }
                    eq 'versionNumber', versionNumberFound
                })

                if (byVersionNumber && byVersionNumber.getDefaultModelCatalogueId(version == null) == Legacy.fixModelCatalogueId(theId).toString()) {
                    return byVersionNumber
                }
            }

            return resource.get(urlId)
        }

        def matchLegacyScheme = theId.toString() =~ /\/(.\w+)\/(\d+)(\.(\d+))?$/

        if (matchLegacyScheme) {
            Long id  = matchLegacyScheme[0][2] as Long
            Integer version = matchLegacyScheme[0][4] as Integer

            if (id > maxCatalogueElementId) {
                return null
            }

            if (version) {
                CatalogueElement result = resource.findByLatestVersionIdAndVersionNumber(id, version)
                if (!result) {
                    result = resource.get(id)
                }
                if (result && result.getLegacyModelCatalogueId(false) == Legacy.fixModelCatalogueId(theId).toString()) {
                    return result
                }
                return null
            }

            CatalogueElement result = resource.findByLatestVersionId(id, [sort: 'versionNumber', order: 'desc']) ?: resource.get(id)

            if (result && Legacy.fixModelCatalogueId(theId).toString().startsWith(result.getLegacyModelCatalogueId(true))) {
                return result
            }

            result = resource.get(id)
            if (result && result.getLegacyModelCatalogueId(true) == Legacy.fixModelCatalogueId(theId).toString()) {
                return result
            }
        }

        return null
    }

    static CatalogueElement getLatestFromCriteria(DetachedCriteria<? extends CatalogueElement> criteria) {
        List<CatalogueElement> elements = criteria.list(sort: 'versionNumber', order: 'desc', max: 1)
        if (elements) {
            return elements.first()
        }
        return null
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

    public DataModel finalizeDataModel(DataModel draft, String version, String revisionNotes, Observer<String> monitor = ProgressMonitor.NOOP) {
        return finalizeDataModel(draft, version, revisionNotes, false, monitor)
    }

    /**
     * @deprecated skipping the eligibility is only available for tests
     */
    public DataModel finalizeDataModel(DataModel draft, String version, String revisionNotes, boolean skipEligibility, Observer<String> monitor = ProgressMonitor.NOOP) {
        // check eligibility for finalization
        if (!skipEligibility) {
            draft.checkFinalizeEligibility(version, revisionNotes)
        }

        if (draft.hasErrors()) {
            monitor.onNext(FriendlyErrors.printErrors("Element is not valid", draft.errors))
            return draft
        }
        return (DataModel) CatalogueElement.withTransaction { TransactionStatus status ->
            auditService.logElementFinalized(draft) {
                DataModel finalized = draft.publish(this, monitor) as DataModel

                if (finalized.hasErrors()) {
                    status.setRollbackOnly()
                    monitor.onNext(FriendlyErrors.printErrors("Element is not valid", finalized.errors))
                }

                finalized.semanticVersion = version
                finalized.revisionNotes = revisionNotes

                finalized.save(deepValidate: false)
            }
        }
    }

    /**
     * @deprecated finalization should only happen on the data model level
     */
    public <E extends CatalogueElement> E finalizeElement(E draft, Observer<String> monitor = ProgressMonitor.NOOP) {
        return (E) CatalogueElement.withTransaction { TransactionStatus status ->
            auditService.logElementFinalized(draft) {
                E finalized = draft.publish(this, monitor) as E
                if (finalized.hasErrors()) {
                    status.setRollbackOnly()
                }
                finalized
            }
        }
    }

    static List<ElementStatus> getStatusFromParams(params, boolean canViewDrafts) {
        if (!params.status) {
            return ImmutableList.copyOf(ElementStatus.values().toList())
        }
        if (params.status == 'active') {
            if (canViewDrafts) {
                return ImmutableList.of(ElementStatus.FINALIZED, ElementStatus.DRAFT)
            }
            return ImmutableList.of(ElementStatus.FINALIZED)
        }
        if (params.status instanceof ElementStatus) {
            return ImmutableList.of(params.status as ElementStatus)
        }
        return ImmutableList.of(ElementStatus.valueOf(params.status.toString().toUpperCase()))
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
            enums[getNormalizedEnumValues(Enumerations.from(row[1]))] << row[0]
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

        if (element.dataModel) {
            element.status = element.dataModel.status
        } else {
            element.status = ElementStatus.FINALIZED
        }
        element.save(flush: true)
        return element
    }

    int countVersions(CatalogueElement catalogueElement) {
        Long id = catalogueElement.getId()

        if (!id) {
            return 1
        }

        Integer count = CacheService.VERSION_COUNT_CACHE.getIfPresent(catalogueElement.getLatestVersionId() ?: id)

        if (count == null) {
            if (!catalogueElement.getLatestVersionId()) {
                count =  1
            } else {
                count = CatalogueElement.countByLatestVersionId(catalogueElement.getLatestVersionId())
            }
            CacheService.VERSION_COUNT_CACHE.put(catalogueElement.getLatestVersionId() ?: id, count)
        }

        return count
    }


    List<Class> collectSubclasses(Class<?> resource) {
        CacheService.SUBCLASSES_CACHE.get(resource) {
            GrailsDomainClass domainClass = grailsApplication.getDomainClass(resource.name) as GrailsDomainClass

            if (domainClass.hasSubClasses()) {
                return [resource] + domainClass.subClasses.collect { it.clazz }
            }

            return [resource]
        }
    }


    public static <T extends CatalogueElement> ListWithTotalAndType<T> getTypeHierarchy(Map<String, Object> params, T element) {
        return Lists.lazy(params, element.getClass() as Class<T>) {
            List<T> typeHierarchy = []

            collectBases(element, typeHierarchy)

            return typeHierarchy
        }
    }

    public <CE extends CatalogueElement> CE changeType(CatalogueElement element, Class<CE> newType) {
        DraftContext context = DraftContext.userFriendly().changeType(element, newType)
        CE newOne = DraftChain.create(element.dataModel, context).changeType(element, this) as CE
        context.resolvePendingRelationships(context.monitor)
        newOne
    }

    private static <T extends CatalogueElement> void collectBases(T element, List<T> collector) {
        for (T base in element.isBasedOn) {
            if (base in collector) {
                continue
            }
            collector << base
            collectBases(base, collector)
        }
    }

}
