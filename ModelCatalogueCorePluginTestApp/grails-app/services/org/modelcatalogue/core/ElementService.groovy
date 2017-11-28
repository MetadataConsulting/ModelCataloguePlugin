package org.modelcatalogue.core

import com.google.common.collect.ImmutableList
import grails.gorm.DetachedCriteria
import grails.util.Environment
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.hibernate.Criteria
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftChain
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.util.ElasticMatchResult
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.Legacy
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.MatchResult
import org.springframework.transaction.TransactionStatus
import rx.Observer as RxObserver

class ElementService implements Publisher<CatalogueElement> {
    static transactional = false

    GrailsApplication grailsApplication
    RelationshipService relationshipService
    SearchCatalogue modelCatalogueSearchService
    SecurityService modelCatalogueSecurityService
    def messageSource
    AuditService auditService
    def sessionFactory
    def elasticSearchService

    DataModelAclService dataModelAclService

//    NONE OF THESE ARE USED OR IMPLEMENTED - Commenting them out - will remove
//    List<CatalogueElement> list(Map params = [:]) {
//        CatalogueElement.findAllByStatusInList(getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/), params)
//    }
//
//    public <E extends CatalogueElement> List<E> list(params = [:], Class<E> resource) {
//        resource.findAllByStatusInList(getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/), params)
//    }
//
//    Long count(params = [:]) {
//        CatalogueElement.countByStatusInList(getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/))
//    }
//
//    public <E extends CatalogueElement> Long count(params = [:], Class<E> resource) {
//        resource.countByStatusInList(getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/))
//    }

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

                dataModelAclService.copyPermissions(dataModel, draft)

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

    public DataModel finalizeDataModel(DataModel draft, String version, String revisionNotes, RxObserver<String> monitor = ProgressMonitor.NOOP) {
        return finalizeDataModel(draft, version, revisionNotes, false, monitor)
    }

    /**
     * @deprecated skipping the eligibility is only available for tests
     */
    public DataModel finalizeDataModel(DataModel draft, String version, String revisionNotes, boolean skipEligibility, RxObserver<String> monitor = ProgressMonitor.NOOP) {
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
    public <E extends CatalogueElement> E finalizeElement(E draft, RxObserver<String> monitor = ProgressMonitor.NOOP) {
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

    static List<ElementStatus> findAllElementStatus(String status, boolean canViewDrafts) {
        if (!status) {
            return ImmutableList.copyOf(ElementStatus.values().toList())
        }
        if (status == 'active') {
            if (canViewDrafts) {
                return ImmutableList.of(ElementStatus.FINALIZED, ElementStatus.DRAFT)
            }
            return ImmutableList.of(ElementStatus.FINALIZED)
        }
        ImmutableList.of(ElementStatus.valueOf(status.toUpperCase()))
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


    Map<Long, Long> findClassesToBeInlined() {
        if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST]) {
            // does not work with H2 database
            log.warn "Trying to find inlined classes in development mode. This feature does not work with H2 database"
            return [:]
        }
        List<DataClass> dataClasses = DataClass.executeQuery("""
            select m
            from DataClass m left join m.incomingRelationships inc
            group by m.name
            having sum(case when inc.relationshipType = :base then 1 else 0 end) = 1
            and sum(case when inc.relationshipType = :hierarchy then 1 else 0 end) = 2
        """, [hierarchy: RelationshipType.hierarchyType, base: RelationshipType.readByName("base")])

        Map<Long, Long> ret = [:]

        for (DataClass dataClass in dataClasses) {
            if (dataClass.ext.from == 'xs:element') {
                ret[dataClass.id] = dataClass.isBasedOn[0].id
            }
        }

        ret
    }

    /**
     * Return classes which are very likely to be duplicates.
     * For classes having same name as at least one other class check if they contains same child classes and data
     * elements. If so return their id and the set of ids of similar classes.
     * @return map with the class id as key and set of ids of duplicate classes as value
     */
    Map<Long, Set<Long>> findDuplicateClassesSuggestions() {
        // TODO: create test
        Object[][] results = DataClass.executeQuery """
            select class1.id, class1.name, rel.relationshipType.name,  rel.destination.name
            from DataClass class1 join class1.outgoingRelationships as rel
            where
                class1.name in (
                    select class2.name from DataClass class2
                    where class2.status in :states
                    group by class2.name
                    having count(class2.id) > 1
                )
            and
                class1.status in :states
            and
                rel.archived = false
            and
                (rel.relationshipType = :containment or rel.relationshipType = :hierarchy)
            order by class1.name asc, class1.dateCreated asc, rel.destination.name asc
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], containment: RelationshipType.readByName('containment'), hierarchy: RelationshipType.readByName('hierarchy')]


        Map<Long, Map<String, Object>> classes = new LinkedHashMap<Long, Map<String, Object>>().withDefault { [id: it, elementNames: new TreeSet<String>(), childrenNames: new TreeSet<String>()] }

        for (Object[] row in results) {
            def info = classes[row[0] as Long]
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

        classes.each { id, info ->
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
    Map<Long, Set<Long>> findDuplicateEnumerationsSuggestions(Long dataModelIdA, Long dataModelIdB) {

        Object[][] results = EnumeratedType.executeQuery """
            select e.id, e.enumAsString
            from EnumeratedType e
            where e.status in :states 
            and e.dataModel.id = :dataModelIdA
            order by e.name
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], dataModelIdA: dataModelIdA]

        Object[][] results2 = EnumeratedType.executeQuery """
            select e.id, e.enumAsString
            from EnumeratedType e
            where e.status in :states 
            and e.dataModel.id = :dataModelIdB
            order by e.name
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], dataModelIdB: dataModelIdB]


        Map<String, Set<Long>> enums = [:].withDefault { [] as TreeSet<Long> }
        Map<String, Set<Long>> enums2 = [:].withDefault { [] as TreeSet<Long> }

        for (Object[] row in results) {
            enums[getNormalizedEnumValues(Enumerations.from(row[1]))] << row[0]
        }

        for (Object[] row in results2) {
            enums2[getNormalizedEnumValues(Enumerations.from(row[1]))] << row[0]
        }

        Map<Long, Set<Long>>  matches = [:]

        enums.each{ String key, Set<Long> values ->
            def found = enums2.get(key)
            if(found) {
                //as this is a data type only match,
                //if the enumeration contains yes / no then we probably don't want to match it, otherwise we get loads of matches that aren't relevant
                if (!key.toLowerCase().contains("yes")) {
                    values.each { val ->
                        matches.put(val, found.asList())
                    }
                }
            }
        }

        matches

    }

    String getNormalizedEnumValues(Map<String, String> enumValues) {
        enumValues.collect { k, v ->
            if (k ==~ /\d+/) {
                return "" + k + v
            }
            return k + v
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


    /**
     * Return dataElement ids which are very likely to be duplicates.
     * Enums are very likely duplicates if they have similar enum values.
     * @return map with the enum id as key and set of ids of duplicate enums as value
     */
    Map<Long, Set<Long>> findDuplicateDataElementSuggestions(DataModel dataModelA, DataModel dataModelB) {
        Map<Long, Set<Long>> elementSuggestions = new LinkedHashMap<Long, Set<Long>>()
        def results = getDataElementsInCommon(dataModelA.id,dataModelB.id)
        if(results.size() > 0){
            results.each{
                def dataElementName = it as String
                Long ida =getDataElementId(dataElementName,dataModelA.id)
                Long idb =getDataElementId(dataElementName,dataModelB.id)
                elementSuggestions.put(ida,idb)
            }
        }
        return elementSuggestions
    }

    /**
     * Return dataElement ids which are very likely to be synonyms using elasticsearch fuzzy matching.
     * @return map with the enum id as key and set of ids of duplicate enums as value
     */
    Set<MatchResult> findFuzzyDataElementSuggestions(DataModel dataModelA, DataModel dataModelB, Long minimumScore = 1) {
        Set<MatchResult> elementSuggestions = []
        Map searchParams = [:]
        //iterate through the data model a
        def elementsToMatch = DataElement.findAllByDataModel(dataModelA)
        elementsToMatch.each{ DataElement de ->
            //set params map
            searchParams.dataModel = dataModelB.id
            searchParams.search = de.name
            searchParams.minScore = minimumScore/100
            def matches = elasticSearchService.fuzzySearch(DataElement, searchParams)
            String message = checkRelatedTo(de, dataModelB)
            matches.getItemsWithScore().each{ item, score ->
                if(!de.relatedTo.contains(item)) {
                    score  = score*100
                    if(score>minimumScore) {
                        elementSuggestions.add(new ElasticMatchResult(dataElementA: de, dataElementB: item , matchScore: score.round(2), message: message))
                    }
                }
            }
        }

        return elementSuggestions
    }

    private String checkRelatedTo(DataElement de, DataModel proposedModel){
        String modelRelatedItems = ""
        de.relatedTo.each{ ce ->
            if(ce.dataModel == proposedModel){
                modelRelatedItems = modelRelatedItems + "Note: $de.name already related to: $ce.name \n "
            }
        }
        modelRelatedItems
    }


    private Long getDataElementId(String dataElementName, Long dataModelId){
        Long dataElementId = 0

        String query = """SELECT catalogue_element.id FROM catalogue_element, data_element   
            WHERE catalogue_element.id = data_element.id
            AND catalogue_element.name = '${dataElementName}'
            AND catalogue_element.data_model_id = ${dataModelId};"""

        final session = sessionFactory.currentSession
        // Create native SQL query.
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
        List results = sqlQuery.list()
        if(results.size() == 0){
            dataElementId = 0
        }else{
            def mid = results[0]
            dataElementId = mid["id"] as Long
        }

        return dataElementId
    }

    private Long getDataClassId(String dataClassName){
        Long dataClassId = 0

        String query = "SELECT catalogue_element.id FROM catalogue_element, data_class  " +
            "WHERE catalogue_element.id = data_class.id" +
            " AND catalogue_element.name = '${dataClassName}' ;"

        final session = sessionFactory.currentSession
        // Create native SQL query.
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
        List results = sqlQuery.list()
        if(results.size() == 0){
            dataClassId = 0
        }else{
            def mid = results[0]
            dataClassId = mid["id"] as Long
        }

        return dataClassId
    }

    private Long getDataTypeId(String dataTypeName){
        Long dataTypeId = 0

        String query = "SELECT catalogue_element.id FROM catalogue_element, data_type  " +
            "WHERE catalogue_element.id = data_type.id" +
            " AND catalogue_element.name = '${dataTypeName}' ;"

        final session = sessionFactory.currentSession
        // Create native SQL query.
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
        List results = sqlQuery.list()
        if(results.size() == 0){
            dataTypeId = 0
        }else{
            def mid = results[0]
            dataTypeId = mid["id"] as Long
        }

        return dataTypeId
    }

    private Long getDataModelId(String dataModelName){
        Long dataModelId = 0
        String query = "SELECT catalogue_element.id FROM catalogue_element, data_model  " +
            "WHERE catalogue_element.id = data_model.id" +
            " AND catalogue_element.name = '${dataModelName}' ;"
        final session = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
        List results = sqlQuery.list()
        if(results.size() == 0){
            dataModelId = 0
        }else{
            def mid = results[0]
            dataModelId = mid["id"] as Long
        }
        return dataModelId
    }
    /**
     * getDataElementsInCommon
     * @param Long
     * @param Long
     * @return List
     */
    private List getDataElementsInCommon(Long dmAId, Long dmBId){


        String query = """  SELECT  catalogue_element.name 
              FROM catalogue_element, data_element 
              WHERE (catalogue_element.id = data_element.id AND catalogue_element.data_model_id =  :dmA) 
              AND catalogue_element.name IN 
              (select  catalogue_element.name 
              from catalogue_element, data_element 
              where (catalogue_element.id = data_element.id AND catalogue_element.data_model_id =  :dmB))"""

        final session = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(query)
        final results = sqlQuery.with {
            setLong('dmA', dmAId)
            setLong('dmB', dmBId)
            list()
        }

        return results
    }

    /**
     * getDataElementsWithFuzzyMatches
     * This will need to be streamed for large datasets
     * @param Long
     * @param Long
     * @return List
     */
    private List<MatchResult> getDataElementsWithFuzzyMatches(Long dmAId, Long dmBId){
        //Map<Long, Set<Long>> fuzzyElementMap = new HashMap<Long, Set<Long>>()
        List<MatchResult> fuzzyElementList = new ArrayList<MatchResult>()
        String query2getAList = """SELECT DISTINCT catalogue_element.id, catalogue_element.name FROM catalogue_element, data_element WHERE data_model_id = ${dmAId}"""
        final session = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(query2getAList)
        sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
        List aList = sqlQuery.list()
        if(aList.size() == 0){
            fuzzyElementList = null
        }else{
            aList.each{
                def aListName = it.name
                def aListId = it.id
                String query2getBList = """SELECT DISTINCT catalogue_element.id, catalogue_element.name FROM catalogue_element, data_element WHERE data_model_id =  ${dmBId}"""
                sqlQuery = session.createSQLQuery(query2getBList)
                sqlQuery.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                List bList = sqlQuery.list()
                if(bList.size() == 0){
                    fuzzyElementList = null
                }else {
                    bList.each {
                        MatchResult suggestedMatches = new MatchResult()
                        suggestedMatches.setDataElementAName(aListName)
                        suggestedMatches.setDataElementAId(aListId as Long)
                        suggestedMatches.setDataElementBName(it.name)
                        suggestedMatches.setDataElementBId(it.id as Long)

                            //we need not just the element match, but also the rating of the match
                            Long matchScore = getNameMetric(suggestedMatches.dataElementAName, suggestedMatches.dataElementBName)
                            //Only accept matches above pre-defined limit
                            if((matchScore > 80)&(matchScore <= 100)){
                                suggestedMatches.setMatchScore(matchScore)
                                fuzzyElementList.add(suggestedMatches)
                                println " Loading Match: ${suggestedMatches.dataElementAName} and ${suggestedMatches.dataElementBName} score is: ${matchScore}"
                            }
                        }
                    }
                }
            }
        return fuzzyElementList
    }

    /**
     * Return dataElement ids which are very likely to be duplicates.
     * Enums are very likely duplicates if they have similar enum values.
     * @return map with the enum id as key and set of ids of duplicate enums as value
     */
    List<MatchResult> findFuzzyDuplicateDataElementSuggestions(String dataModelA, String dataModelB) {
        Long dataModelIdA = getDataModelId(dataModelA)
        Long dataModelIdB = getDataModelId(dataModelB)
        //Map<Long, Set<Long>> elementSuggestions = new LinkedHashMap<Long, Set<Long>>()
        //List<MatchResult> elementSuggestions = new ArrayList<MatchResult>()
        List<MatchResult> results = getDataElementsWithFuzzyMatches(dataModelIdA,dataModelIdB)

        return results
    }
    /**
     * getNameMetric
     * This is a rather simplified metric which takes the Levenstein distance (essentially the
     * number of characters needed to make the two words the same) and makes a percentage out of
     * it. So if you have a word of 10 characters and 2 of them are different then this metric
     * will have them as being 80% (similar)
     * @param String str1
     * @param String str1
     * @return Long
     */
    private static Long getNameMetric(String str1, String str2){
        Long distance = levensteinDistance(str1,str2)
        Long numberOfCharacters = Math.max(str1.length(), str2.length())
        Long metric = Math.abs(((numberOfCharacters - distance)/numberOfCharacters) * 100)
        return metric
    }
    /**
     * levensteinDistance
     * This is a measure of how alike 2 words or phrases are, it is a number which represents the number of changes
     * you need to make to move from string 1 to string 2 - (essentially the
     * number of characters needed to make the two words the same)
     * @param String
     * @param String
     * @return int
     */
    private static int levensteinDistance(String str1, String str2) {
        def str1_len = str1.length()
        def str2_len = str2.length()
        int[][] distance = new int[str1_len + 1][str2_len + 1]
        (str1_len + 1).times { distance[it][0] = it }
        (str2_len + 1).times { distance[0][it] = it }
        (1..str1_len).each { i ->
            (1..str2_len).each { j ->
                distance[i][j] = [distance[i-1][j]+1, distance[i][j-1]+1, str1[i-1]==str2[j-1]?distance[i-1][j-1]:distance[i-1][j-1]+1].min()
            }
        }
        distance[str1_len][str2_len]
    }

}
