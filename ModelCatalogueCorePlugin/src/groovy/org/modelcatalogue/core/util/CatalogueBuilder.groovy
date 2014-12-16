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

    private Set<CatalogueElement> created = []
    private List<Map<Class, CatalogueElement>> contexts = []

    private Set<Class> unclassifiedQueriesFor = [MeasurementUnit]
    private Set<Class> hasUniqueNames = [MeasurementUnit, Classification]
    private Set<Class> createAutomatically = []

    private static final Map LATEST = [sort: 'versionNumber', order: 'asc', max: 1]

    CatalogueBuilder(ClassificationService classificationService) {
        this.classificationService = classificationService
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

        saveIfNeeded model

        classifyIfNeeded model

        withContextElement(Model) {
            saveIfNeeded model
            it.addToParentOf model
        }

        saveIfNeeded model
    }

    DataElement dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        DataElement element = findOrPrepareInstance DataElement, checkAndPrepareParameters(DataElement, parameters, Model)

        withNewContext element, c

        saveIfNeeded element
        classifyIfNeeded element

        if (element.valueDomain == null && ValueDomain in createAutomatically) {
            withNewContext element, {
                valueDomain()
            }
        }

        withContextElement(Model) {
            saveIfNeeded element
            it.addToContains element
        }

        saveIfNeeded element
    }

    ValueDomain valueDomain(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        ValueDomain valueDomain = findOrPrepareInstance ValueDomain, checkAndPrepareParameters(ValueDomain, parameters, DataElement)

        withNewContext valueDomain, c

        saveIfNeeded valueDomain
        classifyIfNeeded valueDomain

        withContextElement(DataElement) {
            it.valueDomain = valueDomain
        }

        if (valueDomain.dataType == null && DataType in createAutomatically) {
            withNewContext valueDomain, {
                dataType()
            }
            saveIfNeeded valueDomain
        }

        saveIfNeeded valueDomain
    }

    DataType dataType(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        checkAndPrepareParameters DataType, parameters, ValueDomain

        DataType dataType = findOrPrepareInstance((parameters.enumerations ? EnumeratedType : DataType), parameters)

        withNewContext dataType, c
        classifyIfNeeded dataType

        withContextElement(ValueDomain) {
            it.dataType = dataType
        }

        saveIfNeeded dataType
    }

    MeasurementUnit measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        MeasurementUnit unit = findOrPrepareInstance MeasurementUnit, checkAndPrepareParameters(MeasurementUnit, parameters), true

        withNewContext unit, c
        classifyIfNeeded unit

        withContextElement(ValueDomain, true) {
            it.unitOfMeasure = unit
        }

        saveIfNeeded unit
    }


    void child(String classification, String name) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToParentOf tryFind(Model, classification, name, null)
        }
    }

    void child(String name) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToParentOf tryFind(Model, name, null)
        }
    }

    void child(Model element) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToParentOf element
        }
    }

    void contains(String classification, String name) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToContains tryFind(DataElement, classification, name, null)
        }
    }

    void contains(String name) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToContains tryFind(DataElement, name, null)
        }
    }

    void contains(DataElement element) {
        withContextElement(Model) {
            saveIfNeeded it
            it.addToContains element
        }
    }

    void basedOn(String classification, String name) {
        withContextElement(CatalogueElement) {
            saveIfNeeded it
            CatalogueElement other = tryFind(it.class, classification, name, null)
            it.addToBasedOn other
            if (it instanceof ValueDomain && other instanceof ValueDomain && !it.dataType) {
                it.dataType = other.dataType
            }
        }
    }

    void basedOn(String name) {
        withContextElement(CatalogueElement) {
            saveIfNeeded it
            CatalogueElement other = tryFind(it.class, name, null)
            it.addToBasedOn other
            if (it instanceof ValueDomain && other instanceof ValueDomain && !it.dataType) {
                it.dataType = other.dataType
            }
        }
    }

    void basedOn(CatalogueElement element) {
        withContextElement(CatalogueElement) {
            saveIfNeeded it
            it.addToBasedOn element
            if (it instanceof ValueDomain && element instanceof ValueDomain && !it.dataType) {
                it.dataType = element.dataType
            }
        }
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
            saveIfNeeded it
            it.ext.put(key, value)
        }
    }

    void ext(Map<String, String> values) {
        withContextElement(CatalogueElement) {
            saveIfNeeded it
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

    private <T extends CatalogueElement> T setContextElement(T contextElement) {
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
        withContextElement(Classification) {
            saveIfNeeded element
            it.addToClassifies element
        }
    }

    /**
     * Executes closure with context element of given type if present.
     * @param contextElementType
     * @param closure
     */
    private <T extends CatalogueElement> void withContextElement(Class<T> contextElementType, boolean update = false, @ClosureParams(value=FirstParam.FirstGenericType) Closure closure) {
        T contextElement = getContextElement(contextElementType)
        if (contextElement) {
            closure(contextElement)
        }
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

    private static <T extends CatalogueElement> T saveIfNeeded(T element) {
        if (!element) {
            return element
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



    private <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name, Object id) {
        Classification classification = tryFindUnclassified(Classification, classificationName, id)
        if (!classification) {
            throw new IllegalArgumentException("Requested classification ${classificationName} is not present in the catalogue!")
        }
        tryFindWithClassification(type, classification, name, id)
    }

    private <T extends CatalogueElement> T tryFind(Class<T> type, Object name, Object id) {
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
        if (!parameters.status) {
            withContextElement(CatalogueElement) {
                parameters.status = it.status
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

        if (!element) {
            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} does not exist yet, creating new one"
            element = type.newInstance(parameters)
        } else {
            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} found"
            element.properties = parameters
        }

        created << element
        element
    }

}


