package org.modelcatalogue.core.util.builder

import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import groovy.transform.CompileDynamic
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.util.StopWatch

@Log4j @GrailsCompileStatic
class CatalogueElementProxyRepository {


    static Set<Class> HAS_UNIQUE_NAMES = new LinkedHashSet<Class>([DataModel])

    static final Set<Class> DATA_TYPE_CLASSES = new LinkedHashSet<Class>([EnumeratedType, ReferenceType, DataType, PrimitiveType])

    static final String AUTOMATIC_NAME_FLAG = '__automatic_name__'
    static final String AUTOMATIC_DESCRIPTION_FLAG = '__automatic_description__'
    static final String MISSING_REFERENCE_ID = "http://www.modelcatalogue.org/builder/#missing_reference_id"

    private static final Map LATEST = [sort: 'versionNumber', order: 'desc', max: 1]
    public static final String SEMANTIC_VERSION = "semanticVersion"

    private final DataModelService dataModelService
    private final ElementService elementService

    Set<Class> unclassifiedQueriesFor = []

    private Set<CatalogueElementProxy> pendingProxies = []
    private Set<Long> elementsUnderControl = []

    private boolean copyRelationships = false
    Long maxCatalogueElementIdAtStart = Long.MAX_VALUE

    private final Map<String, Relationship> createdRelationships = [:]

    ProgressMonitor monitor = ProgressMonitor.NOOP


    CatalogueElementProxyRepository(DataModelService dataModelService, ElementService elementService) {
        this.dataModelService = dataModelService
        this.elementService = elementService
    }

    public void clear() {
        unclassifiedQueriesFor.clear()
        createdRelationships.clear()
        pendingProxies.clear()
        elementsUnderControl.clear()
        copyRelationships = false
        maxCatalogueElementIdAtStart = Long.MAX_VALUE
    }

    public copyRelationships() {
        this.copyRelationships = true
    }

    public isCopyRelationship() {
        this.copyRelationships
    }

    public static boolean equals(CatalogueElementProxy a, CatalogueElementProxy b) {
        if (a == b) {
            return true
        }
        if (a && !b || b && !a) {
            return false
        }
        if (a.modelCatalogueId && a.modelCatalogueId == b.modelCatalogueId) {
            return true
        }
        if (a.domain != b.domain) {
            return false
        }
        if (a.domain == DataModel){
            String a_SemanticVersion = a.getParameter(SEMANTIC_VERSION)
            String b_SemanticVersion = b.getParameter(SEMANTIC_VERSION)
            if(a_SemanticVersion && b_SemanticVersion && a_SemanticVersion != b_SemanticVersion){
                return false
            }
        }

        if (a.domain in HAS_UNIQUE_NAMES) {
            return a.name == b.name
        }

        if (!a.classification || !b.classification) {
            return false
        }

        return equals(a.classification, b.classification) && a.name == b.name
    }

    public Set<CatalogueElement> resolveAllProxies(boolean skipDirtyChecking) {
        StopWatch watch =  new StopWatch('catalogue proxy repository')

        // FIXME: keeping all the created elements eats lot of memory
        Set<CatalogueElement> created = []

        List<CatalogueElement> lastElement = CatalogueElement.list(max: 1, sort: 'id', order: 'desc')
        maxCatalogueElementIdAtStart = lastElement ? lastElement.first().getId() : Long.MAX_VALUE

        watch.start('merging proxies')
        logInfo "(1/6) merging proxies"

        // FIXME: move merging step to separate method
        Set<CatalogueElementProxy> elementProxiesToBeResolved     = []
        Map<String, CatalogueElementProxy> byID     = [:]
        Map<String, CatalogueElementProxy> byName   = [:]

        // FIXME: remove the pending proxy from the map as soon as it's processed
        for (CatalogueElementProxy proxy in pendingProxies) {
            if (proxy.modelCatalogueId) {
                CatalogueElementProxy existing = byID[proxy.modelCatalogueId]

                if (!existing) {
                    byID[proxy.modelCatalogueId] = proxy
                    elementProxiesToBeResolved << proxy
                } else {
                    existing.merge(proxy)
                }
            } else if (proxy.name) {
                String fullName = getFullNameForProxy(proxy, proxy.domain)
                String genericName = getFullNameForProxy(proxy, CatalogueElement)

                CatalogueElementProxy existing = byName[fullName]

                if (!existing && fullName != genericName) {
                    existing = byName[genericName]
                    if (existing && !existing.domain.isAssignableFrom(proxy.domain)) {
                        existing = null
                    }
                }

                if (!existing) {
                    byName[fullName] = proxy
                    byName[genericName] = proxy
                    // it is a set, so if we add it twice it does not matter
                    elementProxiesToBeResolved << proxy
                } else {
                    // must survive double addition
                    existing.merge(proxy)
                }
            } else {
                throw new IllegalStateException("Proxy $proxy does not provide ID nor name")
            }
        }
        watch.stop()

        if (!skipDirtyChecking) {
            // Step 1:check something changed this must run before any other resolution happens
            watch.start('dirty checking')
            logInfo "(2/6) dirty checking"

            Set<CatalogueElementProxy> draftRequiredDataModels = new LinkedHashSet<CatalogueElementProxy>()
            DraftContext context = createDraftContext(collectElementsUnderControl(elementProxiesToBeResolved)).forceNew().withMonitor(new SubprocessMonitor(monitor))

            for (CatalogueElementProxy element in elementProxiesToBeResolved) {
                try {
                    String change = element.changed
                    if (change || element.getParameter('status') == ElementStatus.DRAFT) {
                        if (element.domain == DataModel && change != DefaultCatalogueElementProxy.CHANGE_NEW) {
                            draftRequiredDataModels.add(element)
                        } else if (element.classification) {
                            // FIXME: as long as classification is now proxy we don't need to get it from the map
                            draftRequiredDataModels.add(byName[getFullNameForProxy(element.classification, DataModel)] ?: byName[getFullNameForProxy(element.classification, CatalogueElement)] ?: element.classification)
                        } else {
                            logWarn "Cannot request draft for element without data model: $element"
                        }
                        if (change == DefaultCatalogueElementProxy.CHANGE_TYPE) {
                            context.changeType(element.findExisting() as CatalogueElement, element.domain)
                        }
                    }

                } catch (e) {
                    if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                        logWarn "Reference ${element} not present in the catalogue"
                    } else {
                        throw e
                    }
                }
            }
            watch.stop()

            // Step 2: if something changed, create new versions. if run in one step, it generates false changes
            watch.start('creating drafts')
            logInfo "(3/6) creating drafts"

            for (CatalogueElementProxy proxy in draftRequiredDataModels) {
                DataModel dataModel = tryFindUnclassified(DataModel, proxy.name, proxy.modelCatalogueId) as DataModel
                if (!dataModel) {
                    logWarn "Requested to create draft for Data Model '${proxy.name}' but it does not exist yet"
                    continue
                }
                elementService.createDraftVersion(dataModel, proxy.getParameter('semanticVersion')?.toString() ?: PublishingContext.nextPatchVersion(dataModel.semanticVersion), context)
            }
            watch.stop()
        }

        Set<RelationshipProxy> relationshipProxiesToBeResolved = []

        // Step 3: resolve elements (set properties, update metadata)
        watch.start('resolving elements')
        logInfo "(4/6) resolving elements"
        int numCatalogueElementProxies = elementProxiesToBeResolved.size()
        int elNumberOfPositions = Math.floor(Math.log10(numCatalogueElementProxies)).intValue() + 2
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
            logDebug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${numCatalogueElementProxies.toString().padLeft(elNumberOfPositions, '0')}] Resolving $element"
            try {
                CatalogueElement resolved = element.resolve() as CatalogueElement
                created.add(resolved)
                relationshipProxiesToBeResolved.addAll element.pendingRelationships
                if (element.pendingPolicies) {
                    DataModel dataModel = resolved as DataModel
                    for (String policyName in element.pendingPolicies) {
                        DataModelPolicy policy = DataModelPolicy.findByName(policyName)
                        dataModel.addToPolicies(policy)
                    }
                    FriendlyErrors.failFriendlySave(dataModel)
                }
                logDebug "${'-' * (elNumberOfPositions * 2 + 3)} Resolved as $resolved"
            } catch (e) {
                if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                    logWarn "Reference ${element} not present in the catalogue"
                } else {
                    throw e
                }
            }
        }
        watch.stop()

        // Step 5: resolve pending relationships
        watch.start('resolving relationships')
        Set<Long> resolvedRelationships = []
        logInfo "(5/6) resolving relationships"
        int relNumberOfPositions = Math.floor(Math.log10(relationshipProxiesToBeResolved.size())).intValue() + 2
        relationshipProxiesToBeResolved.eachWithIndex { RelationshipProxy relationshipProxy, i ->
            logDebug "[${(i + 1).toString().padLeft(relNumberOfPositions, '0')}/${relationshipProxiesToBeResolved.size().toString().padLeft(relNumberOfPositions, '0')}] Resolving $relationshipProxy"
            try {
                if (relationshipProxy.source.resolve() == relationshipProxy.destination.resolve()) {
                    logWarn "Ignoring self reference: $relationshipProxy"
                    return
                }
                resolvedRelationships << relationshipProxy.resolve(this)?.getId()
            } catch (e) {
                if (anyCause(e,ReferenceNotPresentInTheCatalogueException)) {
                    logWarn "Some item referred by ${relationshipProxy} not present in the catalogue"
                } else {
                    throw e
                }
            }
        }

       if (!copyRelationships) {
            elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy element, i ->
                if (!element.underControl) {
                    return
                }
                CatalogueElement catalogueElement = element.resolve() as CatalogueElement
                Set<Long> relations = []
                relations.addAll catalogueElement.incomingRelationships*.getId()
                relations.addAll catalogueElement.outgoingRelationships*.getId()

                relations.removeAll resolvedRelationships

                relations.collect { Relationship.get(it) } each {
                    unlink(it)
                }
            }
        }

        watch.stop()

        // Step 6: resolve state changes
        watch.start('resolving state changes')
        logInfo "(6/6) resolving state changes"
        elementProxiesToBeResolved.eachWithIndex { CatalogueElementProxy catalogueElementProxy, i ->
            logDebug "[${(i + 1).toString().padLeft(elNumberOfPositions, '0')}/${elementProxiesToBeResolved.size().toString().padLeft(elNumberOfPositions, '0')}] Resolving status changes for $catalogueElementProxy"

            ElementStatus desiredStatus = catalogueElementProxy.getParameter('status') as ElementStatus

            try {
                CatalogueElement catalogueElement = catalogueElementProxy.resolve() as CatalogueElement

                if (desiredStatus && catalogueElement.status != desiredStatus) {
                    if (desiredStatus == ElementStatus.FINALIZED) {
                        elementService.finalizeElement(catalogueElement)
                        logDebug "... finalized $catalogueElement"
                    } else if (desiredStatus == ElementStatus.DEPRECATED) {
                        elementService.archive(catalogueElement, true)
                        logDebug "... deprecated $catalogueElement"
                    }
                }
            } catch (e) {
                if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                    // already printed
                } else {
                    throw e
                }
            }


        }
        watch.stop()

        logInfo "Proxies resolved:\n${watch.prettyPrint()}"

        created
    }

    protected Set<Long> collectElementsUnderControl(Set<CatalogueElementProxy> elementProxiesToBeResolved) {
        Set<Long> elementsUnderControl = new HashSet<Long>()
        elementProxiesToBeResolved.each { CatalogueElementProxy it ->
            if (!it.underControl) {
                return
            }
            try {
                CatalogueElement e = tryFindWithClassification(it.domain, it.classification ? DataModel.findAllByName(it.classification.name?.toString()) : [], it.name, it.modelCatalogueId)

                if (e) {
                    if (e.getLatestVersionId()) {
                        elementsUnderControl << e.getLatestVersionId()
                    } else {
                        elementsUnderControl << e.getId()
                    }
                }
            } catch (e) {
                if (anyCause(e, ReferenceNotPresentInTheCatalogueException)) {
                    logWarn "Reference ${it} not present in the catalogue"
                } else {
                    throw e
                }
            }
        }
        return elementsUnderControl
    }

    private <T extends CatalogueElement> DraftContext createDraftContext(Set<Long> elementsUnderControl) {
        if (copyRelationships) {
            return DraftContext.userFriendly()
        }
        return DraftContext.importFriendly(elementsUnderControl)
    }

   /* protected static String getGenericNameForProxy(CatalogueElementProxy proxy) {

        if (proxy.domain == DataModel) {
            String semanticVersion = proxy.getParameter("semanticVersion")
            return "${proxy.domain.simpleName}:${semanticVersion}:${proxy.name}"
        }

        return "${proxy.domain.simpleName}:${proxy.classification}:${proxy.name}"

        "${CatalogueElement.simpleName}:${proxy.domain in HAS_UNIQUE_NAMES ? '*' : proxy.classification}:${proxy.name}"
    }*/

    protected static String getFullNameForProxy(CatalogueElementProxy proxy, Class domain) {

        if (proxy.domain == DataModel) {
            String semanticVersion = proxy.getParameter(SEMANTIC_VERSION)
            return "${domain.simpleName}:${semanticVersion}:${proxy.name}"
        }

        return "${domain.simpleName}:${proxy.classification?.name}@${proxy.classification?.getParameter(SEMANTIC_VERSION)}:${proxy.name}"
    }

    protected static String getGenericNameForDataModel(String name) {
        "${DataModel.simpleName}:*:${name}"

    }

    protected static String getFullNameForDataModel(String name) {
        "${DataModel.simpleName}:*:${name}"
    }


    private Relationship unlink(Relationship relationship) {
        elementService.relationshipService.unlink(relationship.source as CatalogueElement, relationship.destination as CatalogueElement, relationship.relationshipType as RelationshipType, relationship.dataModel, relationship.archived)
    }

    public <T extends CatalogueElement> CatalogueElementProxy<T> createProxy(Class<T> domain, Map<String, Object> parameters, boolean underControl = false) {
        CatalogueElementProxy<T> proxy = createAbstractionInternal(domain, parameters, underControl)
        pendingProxies << proxy
        proxy
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionInternal(Class<T> domain, Map<String, Object> parameters, boolean underControl = false) {
        if (parameters.id) {
            return createAbstractionById(domain, parameters.name?.toString(), parameters.id?.toString(), underControl)
        } else if (parameters.classification || parameters.dataModel) {
            def dataModel = parameters.dataModel ?: parameters.classification
            CatalogueElementProxy<DataModel> dataModelProxy = dataModel instanceof CatalogueElementProxy ? dataModel as CatalogueElementProxy : createAbstractionByName(DataModel, dataModel.toString(), false)
            return createAbstractionByClassificationAndName(domain, dataModelProxy, parameters.name?.toString(), underControl)
        } else if (parameters.name) {
            return createAbstractionByName(domain, parameters.name?.toString(), underControl)
        }
        throw new IllegalArgumentException("Cannot create element abstraction from $parameters")
    }


    public <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionById(Class<T> domain, String name, String id, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, id, null, name, underControl)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByClassificationAndName(Class<T> domain, CatalogueElementProxy<DataModel> classification, String name, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, classification, name, underControl)
    }

    private <T extends CatalogueElement> CatalogueElementProxy<T> createAbstractionByName(Class<T> domain, String name, boolean underControl) {
        return new DefaultCatalogueElementProxy<T>(this, domain, null, null, name, underControl)
    }


    public static <T extends CatalogueElement> T save(T element) {
        FriendlyErrors.withFriendlyFailure {
            element.save(/* flush: true, */ failOnError: true, deepValidate: false)
        } as T
    }

    protected  <T extends CatalogueElement> T tryFind(Class<T> type, CatalogueElementProxy<DataModel> dataModel, Object name, Object id) {
        if (type in HAS_UNIQUE_NAMES) {
            return tryFindWithClassification(type, null, name, id)
        }
        String semanticVersion = dataModel.getParameter('semanticVersion')
        if (semanticVersion) {
               return tryFindWithClassification(type, DataModel.findAllByNameAndSemanticVersion(dataModel.name, semanticVersion), name, id)
        }
        tryFindWithClassification(type, DataModel.findAllByName(dataModel.name?.toString()), name, id)
    }

    protected <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, null, name, id)
    }

    DataModel tryFindDataModel(String name, String semanticVersion, String modelCatalogueId) {
        if (!semanticVersion) {
            return tryFindUnclassified(DataModel, name, modelCatalogueId)
        }

        if (modelCatalogueId) {
            DataModel result = DataModel.findByModelCatalogueIdAndSemanticVersion(modelCatalogueId, semanticVersion)
            if (result) {
                return result
            }
        }

        if (name) {
            DataModel result =  DataModel.findByNameAndSemanticVersion(name, semanticVersion)
            if (result) {
                return result
            }
        }

        return null
    }

    public <T extends CatalogueElement> List<CatalogueElementProxy<T>> findExistingProxy(Class<T> domain, String name, String id) {

        pendingProxies.findAll {
            domain.isAssignableFrom(it.domain) && (it.name == name || it.modelCatalogueId == id)
        } as List<CatalogueElementProxy<T>>

    }

    protected <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, List<DataModel> dataModels, Object name, Object id) {
        if (type in DATA_TYPE_CLASSES) {
            type = DataType
        }
        if (id) {
            T result = findById(type, id)
            if (result) {
                return result
            }
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = getNameCriteria(type, name)

        if (dataModels) {
            T result = getLatestFromCriteria(dataModelService.classified(criteria, DataModelFilter.includes(dataModels)))

            if (result) {
                if (!id || !result.hasModelCatalogueId()) {
                    return result
                }
                return null
            }

            // we are looking for results within classification, no way to go if not found
            if (!(type in unclassifiedQueriesFor)) {
                return null
            }
        }

        T result = getLatestFromCriteria(getNameCriteria(type, name), true)

        // nothing found
        if (!result) {
            return null
        }

        // only return unclassified results
        if (result.dataModel && !(type in HAS_UNIQUE_NAMES)) {
            return null
        }

        // return only if there is no id or the modelCatalogueId is null
        if (!id || !result.hasModelCatalogueId()) {
            return result
        }

        // not found
        return null
    }

    @CompileDynamic
    private static <T> DetachedCriteria<T> getNameCriteria(Class<T> type, name) {
        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }
        criteria
    }

    protected <T extends CatalogueElement> T findById(Class<T> type, Object id) {
        elementService.findByModelCatalogueId(type, id?.toString(), maxCatalogueElementIdAtStart)?.asType(type) as T
    }

    private static <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
        Map<String, Object> params = unclassifiedOnly ? LATEST - [max: 1] : LATEST
        List<T> elements = criteria.list(params)
        if (elements) {
            if (!unclassifiedOnly || criteria.persistentEntity.javaClass in HAS_UNIQUE_NAMES) {
                return elements.first() as T
            }
            for (T element in elements) {
                if (!element.dataModel) {
                    return element
                }
            }
        }
        return null
    }


    public <T extends CatalogueElement,U extends CatalogueElement> Relationship resolveRelationship(RelationshipProxy<T,U> proxy) {
        RelationshipType type = RelationshipType.readByName(proxy.relationshipTypeName) as RelationshipType

        T sourceElement = proxy.source.resolve()
        U destinationElement = proxy.destination.resolve()

        if (sourceElement == destinationElement) {
            throw new IllegalStateException("Source and the destinaiton is the same: $sourceElement")
        }

        if (sourceElement.hasErrors()) {
            throw new IllegalStateException(FriendlyErrors.printErrors("Source element $sourceElement contains errors and is not ready to be part of the relationship ${proxy.toString()}", sourceElement.errors))
        }

        if (!sourceElement.readyForQueries) {
            throw new IllegalStateException("Source element $sourceElement is not ready to be part of the relationship ${proxy.toString()}")
        }
        if (destinationElement.hasErrors()) {
            throw new IllegalStateException(FriendlyErrors.printErrors("Destination element $destinationElement contains errors and is not ready to be part of the relationship ${proxy.toString()}", destinationElement.errors))
        }

        if (!destinationElement.readyForQueries) {
            throw new IllegalStateException("Destination element $destinationElement is not ready to be part of the relationship ${proxy.toString()}")
        }

        String hash = PublishingContext.hashForRelationship(sourceElement, destinationElement, type)


        Relationship existing = createdRelationships[hash]

        if (existing) {
            return existing
        }



        Relationship relationship = sourceElement.createLinkTo(destinationElement, type, archived: proxy.archived as Object, resetIndices: true, skipUniqueChecking: (proxy.source.new || proxy.destination.new) as Object, ignoreRules: sourceElement.status == ElementStatus.DEPRECATED)

        if(relationship.relationshipType == RelationshipType.supersessionType) {
            if(!relationship.source.latestVersionId){
                relationship.source.latestVersionId = relationship.source.id
                FriendlyErrors.failFriendlySave(relationship.source)
            }
            if(!relationship.destination.latestVersionId){
                relationship.destination.latestVersionId = relationship.source.latestVersionId
                FriendlyErrors.failFriendlySave(relationship.destination)
            }


            if(HibernateHelper.getEntityClass(relationship.source) == DataModel){

                // for each element which does the source data model declare
                for (CatalogueElement element in (relationship.source as DataModel).declares) {
                    if(!element.latestVersionId){
                        element.latestVersionId = element.id
                        FriendlyErrors.failFriendlySave(element)
                    }

                    CatalogueElement other = CatalogueElement.findByNameAndDataModel(element.name, relationship.destination)
                    if (other && !other.latestVersionId) {
                        other.latestVersionId = element.latestVersionId
                        FriendlyErrors.failFriendlySave(other)
                    }
                }
            }
        }



        createdRelationships[hash] = relationship

        return relationship
    }

    private static boolean anyCause(Throwable th, Class<? extends Throwable> error) {
        if (error.isInstance(th)) {
            return true
        }
        if (!th.cause) {
            return false
        }
        return anyCause(th.cause, error)
    }

    @CompileDynamic
    static <T extends CatalogueElement> T findByMissingReferenceId(String missingReferenceId) {
        DetachedCriteria<CatalogueElement> criteria = new DetachedCriteria<CatalogueElement>(CatalogueElement).build {
            extensions {
                eq 'name', MISSING_REFERENCE_ID
                eq 'extensionValue', missingReferenceId
            }
        }
        getLatestFromCriteria(criteria) as T
    }

    void logDebug(String string) {
        monitor.onNext(string)
        log.debug string
    }

    void logInfo(String string) {
        monitor.onNext(string)
        log.info string
    }

    void logWarn(String string) {
        monitor.onNext(string)
        log.warn string
    }


}
