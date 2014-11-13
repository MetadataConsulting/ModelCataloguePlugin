package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import groovy.transform.stc.*
import org.modelcatalogue.core.*

// TODO: late binding of properties to minimize writes
class CatalogueBuilder {

    private static Set<Class> SUPPORTED_AS_CONTEXT  = [CatalogueElement, Classification, ValueDomain, DataType, Model, MeasurementUnit, DataElement]
    private static Set<Class> SUPPORTED_FOR_AUTO    = [DataType, ValueDomain]

    private ClassificationService classificationService

    private Set<CatalogueElement> created = []
    private List<Map<Class, CatalogueElement>> contexts = []

    private Set<Class> unclassifiedQueriesFor = []
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
        assert parameters.name : "You must provide the name of the classification"
        Classification classification = tryFindUnclassified(Classification, parameters.name)

        if (!classification) {
            classification = new Classification(parameters).save(failOnError: true)
        } else {
            classification.properties = parameters
            saveIfDirty classification
        }

        created << classification


        executeWithContext classification, c
        unclassifiedQueriesFor.clear()

        saveIfDirty classification
    }

    Model model(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        assert parameters.name : "You must provide the name of the model"

        Model model = tryFind Model, parameters.name

        if (!model) {
            model = new Model(parameters).save(failOnError: true)
        } else {
            model.properties = parameters
            saveIfDirty model
        }

        created << model
        executeWithContext model, c

        saveIfDirty model
        classifyIfNeeded model

        withContextElement(Model) {
            saveIfDirty model
            it.addToParentOf model
        }

        model
    }

    DataElement dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        assert parameters.name : "You must provide the name of the data element"

        DataElement element = tryFind DataElement, parameters.name

        if (!element) {
            element = new DataElement(parameters).save(failOnError: true)
        } else {
            element.properties = parameters
            saveIfDirty element
        }

        created << element
        executeWithContext element, c

        saveIfDirty element
        classifyIfNeeded element

        if (element.valueDomain == null && ValueDomain in createAutomatically) {
            executeWithContext element, {
                valueDomain()
            }
            saveIfDirty element
        }

        withContextElement(Model) {
            saveIfDirty element
            it.addToContains element
        }

        element
    }

    ValueDomain valueDomain(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        withContextElement(DataElement) {
            if (!parameters.name) {
                parameters.name = it.name
            }
            if (!parameters.description) {
                parameters.description = it.description
            }
        }

        assert parameters.name : "You must provide the name of the value domain"

        ValueDomain valueDomain = tryFind ValueDomain, parameters.name

        if (!valueDomain) {
            valueDomain = new ValueDomain(parameters).save(failOnError: true)
        } else {
            valueDomain.properties = parameters
            saveIfDirty valueDomain
        }

        created << valueDomain
        executeWithContext valueDomain, c

        saveIfDirty valueDomain
        classifyIfNeeded valueDomain

        if (valueDomain.dataType == null && DataType in createAutomatically) {
            executeWithContext valueDomain, {
                dataType()
            }
            saveIfDirty valueDomain
        }

        valueDomain
    }

    DataType dataType(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        withContextElement(ValueDomain) {
            if (!parameters.name) {
                parameters.name = it.name
            }
            if (!parameters.description) {
                parameters.description = it.description
            }
        }
        assert parameters.name : "You must provide the name of the data type"

        DataType dataType = tryFind DataType, parameters.name



        if (!dataType) {
            dataType = parameters.enumerations ?
                    new EnumeratedType(parameters).save(failOnError: true) :
                    new DataType(parameters).save(failOnError: true)
        } else {
            dataType.properties = parameters
            saveIfDirty dataType
        }

        created << dataType
        executeWithContext dataType, c
        classifyIfNeeded dataType

        withContextElement(ValueDomain, true) {
            it.dataType = dataType
        }

        saveIfDirty dataType
    }

    MeasurementUnit measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        assert parameters.name : "You must provide the name of the measurement unit type"

        MeasurementUnit unit = tryFind MeasurementUnit, parameters.name



        if (!unit) {
            unit = new MeasurementUnit(parameters).save(failOnError: true)
        } else {
            unit.properties = parameters
            saveIfDirty unit
        }

        created << unit
        executeWithContext unit, c
        classifyIfNeeded unit

        withContextElement(ValueDomain, true) {
            it.unitOfMeasure = unit
        }

        saveIfDirty unit
    }

    void basedOn(String classification, String name) {
        withContextElement(CatalogueElement) {
            saveIfDirty it
            it.addToBasedOn tryFind(it.class, classification, name)
        }
    }
    void basedOn(String name) {
        withContextElement(CatalogueElement) {
            saveIfDirty it
            it.addToBasedOn tryFind(it.class, name)
        }
    }

    void description(String description) { setStringValue('description', description) }
    void rule(String rule) { setStringValue('rule', rule) }
    void id(String rule) { setStringValue('modelCatalogueId', rule) }

    void ext(String key, String value) {
        withContextElement(CatalogueElement) {
            it.ext.put(key, value)
        }
    }

    void ext(Map<String, String> values) {
        withContextElement(CatalogueElement) {
            it.ext.putAll(values)
        }
    }


    // following cannot be static as it would no longer be available for the scripts
    Class<Classification> getClassification() { Classification }
    Class<Model> getModel() { Model }
    Class<DataElement> getDataElement() { DataElement }
    Class<ValueDomain> getValueDomain() { ValueDomain }
    Class<DataType> getDataType() { DataType }
    Class<MeasurementUnit> getMeasurementUnit() { MeasurementUnit }

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

    private <T extends CatalogueElement>  void executeWithContext(T contextElement, Closure c) {
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
            saveIfDirty element
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
            if (update) {
                contextElement.save(failOnError: true)
            }
        }
    }

    private void setStringValue(String name, String value) {
        withContextElement(CatalogueElement) {
            if (!it.hasProperty(name)) {
                throw new IllegalArgumentException("There is no property '$name' in $it")
            }
            it.setProperty(name, value?.stripIndent()?.trim())
        }
    }

    private static <T extends CatalogueElement> T saveIfDirty(T element) {
        if (element.dirty) {
            return element.save(failOnError: true) as T
        }
        return element
    }



    private <T extends CatalogueElement> T tryFind(Class<T> type, Object classificationName, Object name) {
        Classification classification = tryFindUnclassified(Classification, classificationName)
        if (!classification) {
            throw new IllegalArgumentException("Requested classification ${classificationName} is not present in the catalogue!")
        }
        tryFindWithClassification(type, classification, name)
    }

    private <T extends CatalogueElement> T tryFind(Class<T> type, Object name) {
        tryFindWithClassification(type, getContextElement(Classification), name)
    }

    private <T extends CatalogueElement> T tryFindUnclassified(Class<T> type, Object name) {
        tryFindWithClassification(type, null, name)
    }

    private <T extends CatalogueElement> T tryFindWithClassification(Class<T> type, Classification classification, Object name) {
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
        if (result.classifications) {
            return null
        }

        // ok unclassified, return it
        return result
    }

    private static <T extends CatalogueElement> T getLatestFromCriteria(DetachedCriteria<T> criteria, boolean unclassifiedOnly = false) {
        Map<String, Object> params = unclassifiedOnly ? LATEST - [max: 1] : LATEST
        List<T> elements = criteria.list(params)
        if (elements) {
            if (!unclassifiedOnly) {
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

}
