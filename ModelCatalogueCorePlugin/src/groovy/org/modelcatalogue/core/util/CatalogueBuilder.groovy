package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import groovy.transform.stc.*
import org.modelcatalogue.core.*

class CatalogueBuilder {

    private Set<CatalogueElement> created = []
    private Map<Class, CatalogueElement> context = [:]

    private ClassificationService classificationService
    private Set<Class> unclassifiedQueriesFor = []
    private Set<Class> createAutomatically = []

    private static final Map LATEST = [sort: 'versionNumber', order: 'asc']

    CatalogueBuilder(ClassificationService classificationService) {
        this.classificationService = classificationService
    }

    Set<CatalogueElement> build(@DelegatesTo(CatalogueBuilder) Closure c) {
        created = []
        CatalogueBuilder self = this
        Classification.withTransaction {
            self.with c
        }
        created
    }

    Classification classification(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        assert parameters.name : "You must provide the name of the classification"
        Classification classification = Classification.findByName(parameters.name as String, LATEST)

        if (!classification) {
            classification = new Classification(parameters).save(failOnError: true)
        } else {
            classification.properties = parameters
            saveIfDirty classification
        }

        created << classification


        executeWithContext Classification, classification, c
        unclassifiedQueriesFor.clear()

        saveIfDirty classification

        classification
    }

    ValueDomain valueDomain(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        assert parameters.name : "You must provide the name of the value domain"

        ValueDomain valueDomain = tryFind ValueDomain, parameters.name

        if (!valueDomain) {
            valueDomain = new ValueDomain(parameters).save(failOnError: true)
        } else {
            valueDomain.properties = parameters
            saveIfDirty valueDomain
        }

        created << valueDomain
        executeWithContext ValueDomain, valueDomain, c

        saveIfDirty valueDomain
        classifyIfNeeded valueDomain

        if (valueDomain.dataType == null && DataType in createAutomatically) {
            executeWithContext ValueDomain, valueDomain, {
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
        executeWithContext DataType, dataType, c
        classifyIfNeeded dataType

        withContextElement(ValueDomain, true) {
            it.dataType = dataType
        }

        saveIfDirty dataType

        dataType
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


    static Class<ValueDomain> getValueDomain() { ValueDomain }
    static Class<DataType> getDataType() { DataType }

    public <T extends CatalogueElement> void globalSearchFor(Class<T> type){
        unclassifiedQueriesFor << type
    }

    public void automatic(Class<DataType> type){
        if (type != DataType) {
            throw new IllegalArgumentException("Only data types can be created automatically at the moment")
        }
        createAutomatically << type
    }

    public Set<CatalogueElement> getLastCreated() {
      new HashSet<CatalogueElement>(created)
    }

    private <T extends CatalogueElement>  void executeWithContext(Class<T> contextElementType, T contextElement, Closure c) {
        setContextElement(contextElement)
        with c
        clearContextElement(contextElementType)
    }

    private <T extends CatalogueElement> T setContextElement(T contextElement) {
        if (!contextElement) {
            return contextElement
        }
        for (Class type in [CatalogueElement, Classification, ValueDomain, DataType]) {
            if (contextElement.instanceOf(type)) {
                context[type] = contextElement
            }
        }
        contextElement
    }

    private void clearContextElement(Class contextElementType) {
        for (Class type in [CatalogueElement, Classification, ValueDomain, DataType]) {
            if (contextElementType.isAssignableFrom(type)) {
                context.remove(type)
            }
        }
    }

    private <T extends CatalogueElement> T getContextElement(Class<T> contextElementType = CatalogueElement) {
        context[contextElementType] as T
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

    private static void saveIfDirty(CatalogueElement element) {
        if (element.dirty) {
            element.save(failOnError: true)
        }
    }

    private <T extends CatalogueElement> T tryFind(Class<T> type, Object name) {
        if (!name) {
            // should not happen
            return null
        }

        T result

        Classification classification = getContextElement(Classification)

        DetachedCriteria<T> criteria = new DetachedCriteria<T>(type).build {
            eq 'name', name.toString()
        }

        if (classification) {
            result = classificationService.classified(criteria, [classification]).get(LATEST)

            if (result) {
                return result
            }

            if (!(type in unclassifiedQueriesFor)) {
                return null
            }
        }
        return criteria.get(LATEST)
    }

}
