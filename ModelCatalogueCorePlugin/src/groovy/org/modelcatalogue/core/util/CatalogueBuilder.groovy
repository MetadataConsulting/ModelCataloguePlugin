package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j

import org.modelcatalogue.core.*
import groovy.transform.stc.*

@Log4j
class CatalogueBuilder {

    private static Set<Class> SUPPORTED_AS_CONTEXT  = [CatalogueElement, Classification, ValueDomain, DataType, Model, MeasurementUnit, DataElement]
    private static Set<Class> SUPPORTED_FOR_AUTO    = [DataType, ValueDomain]

    private ClassificationService classificationService
    private ElementService elementService

    private Set<CatalogueElement> created = []
    private List<Map<Class, CatalogueElement>> contexts = []

    private Set<Class> unclassifiedQueriesFor = [MeasurementUnit]
    private Set<Class> hasUniqueNames = [MeasurementUnit, Classification]
    private Set<Class> createAutomatically = []

    private static final Map LATEST = [sort: 'versionNumber', order: 'asc', max: 1]

    CatalogueBuilder(ClassificationService classificationService, ElementService elementService) {
        this.classificationService = classificationService
        this.elementService = elementService
    }

    Set<CatalogueElement> build(@DelegatesTo(CatalogueBuilder) Closure c) {
        reset()
        CatalogueBuilder self = this
        self.with c
        created
    }

    Classification classification(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        Classification classification = findOrPrepareInstance Classification, checkAndPrepareParameters(Classification, parameters)

        withNewContext classification, c

        unclassifiedQueriesFor = [MeasurementUnit]

        saveIfNeeded classification
    }

    Model model(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        Model model = findOrPrepareInstance Model, checkAndPrepareParameters(Model, parameters, Classification)

        withNewContext model, c

        model = saveIfNeeded model

        classifyIfNeeded model

        withContextElement(Model) {
            child model
        }

        saveIfNeeded model
    }

    DataElement dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        DataElement element = findOrPrepareInstance DataElement, checkAndPrepareParameters(DataElement, parameters, Model)

        withNewContext element, c

        element = saveIfNeeded element

        classifyIfNeeded element

        if (element.valueDomain == null && ValueDomain in createAutomatically) {
            withNewContext element, {
                valueDomain()
            }
        }

        withContextElement(Model) {
            contains element
        }

        saveIfNeeded element
    }

    ValueDomain valueDomain(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        ValueDomain domain = findOrPrepareInstance ValueDomain, checkAndPrepareParameters(ValueDomain, parameters, DataElement)

        withNewContext domain, c

        domain = saveIfNeeded domain

        classifyIfNeeded domain

        withContextElement(DataElement) {
            it.valueDomain = domain
        }

        if (domain.dataType == null && DataType in createAutomatically) {
            withNewContext domain, {
                dataType()
            }
            domain = saveIfNeeded domain
        }

        saveIfNeeded domain
    }

    DataType dataType(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        checkAndPrepareParameters DataType, parameters, ValueDomain

        DataType dataType = findOrPrepareInstance((parameters.enumerations ? EnumeratedType : DataType), parameters)

        withNewContext dataType, c

        dataType = saveIfNeeded dataType

        classifyIfNeeded dataType

        withContextElement(ValueDomain) {
            it.dataType = dataType
        }

        saveIfNeeded dataType
    }

    MeasurementUnit measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        MeasurementUnit unit = findOrPrepareInstance MeasurementUnit, checkAndPrepareParameters(MeasurementUnit, parameters), true

        withNewContext unit, c

        unit = saveIfNeeded unit

        classifyIfNeeded unit

        withContextElement(ValueDomain, true) {
            it.unitOfMeasure = unit
        }

        saveIfNeeded unit
    }


    void child(String classification, String name) {
        child(assertFound(tryFind(Model, classification, name, null), "Cannot add child as Model $name in $classification does not exist") as Model)
    }

    void child(String name) {
        child(assertFound(tryFind(Model, name, null), "Cannot add child as Model $name does not exist") as Model)
    }

    void child(Model model) {
        rel "hierarchy" to model
    }


    void contains(String classification, String name) {
        contains(assertFound(tryFind(DataElement, classification, name, null), "Cannot add element as Data Element $name in $classification does not exist") as DataElement)
    }

    void contains(String name) {
        contains(assertFound(tryFind(DataElement, name, null), "Cannot add element as Data Element $name does not exist") as DataElement)
    }

    void contains(DataElement element) {
        rel "containment" to element
    }

    void basedOn(String classification, String name) {
        withContextElement(CatalogueElement) {
            basedOn(assertFound(tryFind(it.class, classification, name, null), "Cannot add base as element $name in $classification does not exist") as CatalogueElement)
        }
    }

    void basedOn(String name) {
        withContextElement(CatalogueElement) {
            basedOn(assertFound(tryFind(it.class, name, null), "Cannot add base as element $name does not exist") as CatalogueElement)
        }
    }

    void basedOn(CatalogueElement element) {
        rel "base" from element

        withContextElement(ValueDomain) {
            if (element instanceof ValueDomain && !it.dataType && !(DataType in createAutomatically)) {
                it.dataType = element.dataType
            }
        }
    }
    RelationshipBuilder rel(String relationshipTypeName) {
        return new RelationshipBuilder(this,relationshipTypeName)
    }

    void description(String description) { setStringValue('description', description) }
    void rule(String rule) { setStringValue('rule', rule) }
    void regex(String regex) { setStringValue('regexDef', regex) }

    /**
     * Sets the id of the current element.
     *
     * This id is not used for queries. If you want to query by model catalogue id, send it as parameter in
     * the domain call such as model(id: 'http://www.example.com/foo').
     * @param id
     */
    void id(String id) { setStringValue('modelCatalogueId', id) }

    void status(ElementStatus status) {
        withContextElement(CatalogueElement) {
            it.status = status
        }
    }

    void ext(String key, String value) {
        withContextElement(CatalogueElement) {
            it = saveIfNeeded it
            it.ext.put(key, value)
        }
    }

    void ext(Map<String, String> values) {
        withContextElement(CatalogueElement) {
            it = saveIfNeeded it
            it.ext.putAll(values)
        }
    }


    static Class<Classification> getClassification() { Classification }
    static Class<Model> getModel() { Model }
    static Class<DataElement> getDataElement() { DataElement }
    static Class<ValueDomain> getValueDomain() { ValueDomain }
    static Class<DataType> getDataType() { DataType }
    static Class<MeasurementUnit> getMeasurementUnit() { MeasurementUnit }

    static ElementStatus getDraft() { ElementStatus.DRAFT }
    static ElementStatus getDeprecated() { ElementStatus.DEPRECATED }
    static ElementStatus getFinalized() { ElementStatus.FINALIZED }

    public <T extends CatalogueElement> void globalSearchFor(Class<T> type){
        unclassifiedQueriesFor << type
    }

    public <T extends CatalogueElement> void automatic(Class<T> type){

        if (!(type in SUPPORTED_FOR_AUTO)) {
            throw new IllegalArgumentException("Only supported values are ${SUPPORTED_FOR_AUTO.collect{GrailsNameUtils.getPropertyName(it)}.join(', ')}")
        }
        createAutomatically << type
    }

    public Set<CatalogueElement> getLastCreated() {
      new HashSet<CatalogueElement>(created)
    }

    // helper methods

    private <T extends CatalogueElement>  void withNewContext(T contextElement, Closure c) {
        // save current element if dirty
        withContextElement(CatalogueElement) {
            saveIfNeeded it
        }

        pushContext()
        setContextElement(contextElement)
        with c
        popContext()
    }

    private void pushContext() {
        contexts.push([:])
    }

    private void popContext() {
        contexts.pop()
    }

    protected <T extends CatalogueElement> T setContextElement(T contextElement) {
        if (!contextElement) {
            return contextElement
        }
        for (Class type in SUPPORTED_AS_CONTEXT) {
            if (contextElement.instanceOf(type)) {
                contexts.last()[type] = contextElement
            }
        }
        contextElement
    }

    protected <T extends CatalogueElement> T replaceContextElement(T contextElement, T old) {
        if (contextElement == old) {
            return contextElement
        }
        if (!old) {
            return old
        }

        contexts.each {
            Map<Class, CatalogueElement> replacements = [:]
            it.each { key, value ->
                if (value == old) {
                    replacements[key] = contextElement
                }
            }
            it.putAll replacements
        }

        contextElement
    }

    private <T extends CatalogueElement> T getContextElement(Class<T> contextElementType = CatalogueElement) {
        for (Map<Class, CatalogueElement> context in contexts.reverse()) {
            T result = context[contextElementType] as T
            if (result) {
                return result
            }
        }
        return null
    }

    private void classifyIfNeeded(CatalogueElement element) {
        if (element instanceof Classification) {
            return
        }
        try {
            rel "classification" to element
        } catch (IllegalStateException ignored) {
            // no classificaiton found in context
        }
    }

    /**
     * Executes closure with context element of given type if present.
     * @param contextElementType
     * @param closure
     */
    private <T extends CatalogueElement> WithOptionalOrClause withContextElement(Class<T> contextElementType, boolean update = false, @ClosureParams(value=FirstParam.FirstGenericType) Closure closure) {
        T contextElement = getContextElement(contextElementType)
        if (contextElement) {
            closure(contextElement)
            return WithOptionalOrClause.NOOP
        }
        DefaultWithOptionalOrClause.INSTANCE
    }

    private void setStringValue(String name, String value) {
        if (!value) {
            return
        }
        withContextElement(CatalogueElement) {
            if (!it.hasProperty(name)) {
                throw new IllegalArgumentException("There is no property '$name' in $it")
            }
            it.setProperty(name, value?.stripIndent()?.trim())
        }
    }

    protected <T extends CatalogueElement> T saveIfNeeded(T element) {
        if (!element) {
            return element
        }

        if (element.dirty && !('status' in element.dirtyPropertyNames) && (element.status == ElementStatus.FINALIZED || element.status == ElementStatus.DEPRECATED)) {
            return createNewVersion(element, element.dirtyPropertyNames as Set<String>)
        }

        if (element.dirty) {
            log.info "Persisting changes ${element.dirtyPropertyNames} of $element"
            return element.save(failOnError: true, flush: true) as T
        } else if (!element.attached) {
            log.info "Persisting $element"
            return element.save(failOnError: true, flush: true) as T
        }

        return element
    }

    protected <T extends CatalogueElement> T createNewVersion(T element, Set<String> changes) {
        T newVersion = elementService.createDraftVersion(element)
        replaceContextElement(newVersion, element)
        for (String propertyName in element.dirtyPropertyNames) {
            newVersion.setProperty(propertyName, element.getProperty(propertyName))
        }
        log.info "Created new version for ${GrailsNameUtils.getNaturalName(element.class.simpleName)} with name ${newVersion.name}. New version differs in ${changes}."
        element.refresh()
        return newVersion.save(failOnError: true, flush: true) as T
    }


    protected <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name, Object id) {
        Classification classification = tryFindUnclassified(Classification, classificationName, id)
        if (!classification) {
            throw new IllegalArgumentException("Requested classification ${classificationName} is not present in the catalogue!")
        }
        tryFindWithClassification(type, classification, name, id)
    }

    protected <T extends CatalogueElement> T tryFind(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, saveIfNeeded(getContextElement(Classification)), name, id)
    }

    private <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name, Object id) {
        tryFindWithClassification(type, null, name, id)
    }

    private <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, Classification classification, Object name, Object id) {
        if (id) {
            DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
                eq 'modelCatalogueId', id.toString()
            }
            return getLatestFromCriteria(criteria)
        }
        if (!name) {
            return null
        }

        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }

        if (classification) {
            T result = getLatestFromCriteria(classificationService.classified(criteria, [classification]))

            if (result) {
                return result
            }

            // we are looking for results within classification, no way to go if not found
            if (!(type in unclassifiedQueriesFor)) {
                return null
            }
        }

        T result = getLatestFromCriteria(criteria, true)

        // nothing found
        if (!result) {
            return null
        }

        // only return unclassified results
        if (result.classifications && !(type in hasUniqueNames)) {
            return null
        }

        // ok unclassified, return it
        return result
    }

    private <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
        Map<String, Object> params = unclassifiedOnly ? LATEST - [max: 1] : LATEST
        List<T> elements = criteria.list(params)
        if (elements) {
            if (!unclassifiedOnly || criteria.persistentEntity.javaClass in hasUniqueNames) {
                return elements.first()
            }
            for (T element in elements) {
                if (!element.classifications) {
                    return element
                }
            }
        }
        return null
    }

    private void reset() {
        contexts = []
        created = []
        unclassifiedQueriesFor = []
        createAutomatically = []
    }

    private <T extends CatalogueElement, E extends CatalogueElement> Map<String, Object> checkAndPrepareParameters(Class<T> type, Map<String, Object> parameters, Class<E> inheritFrom = null) {
        if (type in SUPPORTED_FOR_AUTO) {
            withContextElement(inheritFrom) {
                if (!parameters.name) {
                    parameters.name = it.name
                }
                if (!parameters.description) {
                    parameters.description = it.description
                }
            }
        }
        assert parameters.name : "You must provide the name of the ${GrailsNameUtils.getNaturalName(type.simpleName)}"

        parameters.modelCatalogueId = parameters.id

        parameters
    }

    private <T extends CatalogueElement> T findOrPrepareInstance(Class<T> type, Map<String, Object> parameters, boolean unclassified = false) {
        T element
        if (unclassified) {
            element = tryFindUnclassified(type, parameters.name, parameters.id)
        } else if (parameters.classification) {
            element = tryFind(type, parameters.classification, parameters.name, parameters.id)
        } else {
            element = tryFind(type, parameters.name, parameters.id)
        }

        parameters.remove 'id'
        parameters.remove 'classification'

        if (!element) {
            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} does not exist yet, creating new one"
            element = type.newInstance(parameters)
        } else {
            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} found"
            Closure propChanged = { String key, Object value ->
                // if something has changed
                if (element.hasProperty(key) && element.getProperty(key) != value) {
                    if (key == 'modelCatalogueKey' && value == null && element.defaultModelCatalogueId == element.modelCatalogueId) {
                        return false
                    }
                    if (key == 'status' && value == null && element.status == ElementStatus.DRAFT) {
                        return false
                    }
                    log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} has changed. At least $key is different (new value is $value)"
                    return true
                }
                return false
            }
            if (parameters.any(propChanged)) {
                if (element.status == ElementStatus.FINALIZED || element.status == ElementStatus.DEPRECATED) {
                    Set<String> changed = properties.findAll(propChanged).keySet()
                    element = createNewVersion(element, changed)
                }
                element.properties = parameters
            }

        }

        created << element
        element
    }

    protected static <T extends CatalogueElement> T assertFound(T element, String notFoundMessage = "Element supposed to be found but it does not exist") {
        if (!element) {
            throw new IllegalStateException(notFoundMessage)
        }
        element
    }

}


class RelationshipBuilder {

    CatalogueBuilder catalogueBuilder
    RelationshipType type

    RelationshipBuilder(CatalogueBuilder catalogueBuilder, String type) {
        if (catalogueBuilder == null) {
            throw new IllegalArgumentException("CatalogueBuilder cannot be null!")
        }
        RelationshipType relationshipType = RelationshipType.findByName(type)
        if (relationshipType == null) {
            throw new IllegalArgumentException("Relationship type $type does not exist!")
        }
        this.catalogueBuilder = catalogueBuilder
        this.type = relationshipType
    }

    void to(String classification, String name) {
        to CatalogueBuilder.assertFound(catalogueBuilder.tryFind(type.destinationClass, classification, name, null), "Cannot create relationship ${type.name} as ${GrailsNameUtils.getNaturalName(GrailsNameUtils.getPropertyName(type.destinationClass))} $name in $classification does not exist")
    }

    void to(String name) {
        to CatalogueBuilder.assertFound(catalogueBuilder.tryFind(type.destinationClass, name, null), "Cannot create relationship ${type.name} as ${GrailsNameUtils.getNaturalName(GrailsNameUtils.getPropertyName(type.destinationClass))} $name does not exist")
    }

    void to(CatalogueElement element) {
        catalogueBuilder.withContextElement(type.sourceClass) {
            assert catalogueBuilder.saveIfNeeded(it).createLinkTo(catalogueBuilder.saveIfNeeded(element), type).errors.errorCount == 0
        } or {
            throw new IllegalStateException("There is no contextual element available of type $type.sourceClass")
        }
    }

    void from(String classification, String name) {
        from CatalogueBuilder.assertFound(catalogueBuilder.tryFind(type.sourceClass, classification, name, null), "Cannot create relationship ${type.name} as ${GrailsNameUtils.getNaturalName(GrailsNameUtils.getPropertyName(type.destinationClass))} $name in $classification does not exist")
    }

    void from(String name) {
        from CatalogueBuilder.assertFound(catalogueBuilder.tryFind(type.sourceClass, name, null), "Cannot create relationship ${type.name} as ${GrailsNameUtils.getNaturalName(GrailsNameUtils.getPropertyName(type.destinationClass))} $name does not exist")
    }

    void from(CatalogueElement element) {
        catalogueBuilder.withContextElement(type.destinationClass) {
            assert catalogueBuilder.saveIfNeeded(it).createLinkFrom(catalogueBuilder.saveIfNeeded(element), type).errors.errorCount == 0
        } or {
            throw new IllegalStateException("There is no contextual element available of type $type.destinationClass")
        }
    }

}

interface WithOptionalOrClause {
    static WithOptionalOrClause NOOP = {}
    void or(Closure orClosure)
}

enum DefaultWithOptionalOrClause implements WithOptionalOrClause {

    INSTANCE

    void or(Closure c) {
        c()
    }
}

