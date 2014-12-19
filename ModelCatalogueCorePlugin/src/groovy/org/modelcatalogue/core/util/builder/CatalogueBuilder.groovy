package org.modelcatalogue.core.util.builder

import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j

import org.modelcatalogue.core.*

@Log4j
class CatalogueBuilder {

    private static Set<Class> SUPPORTED_FOR_AUTO    = [DataType, EnumeratedType, ValueDomain]

    private ClassificationService classificationService
    private ElementService elementService
    private CatalogueElementProxyRepository repository

    private CatalogueBuilderContext context = new CatalogueBuilderContext()
    private Set<CatalogueElement> created = []
    private Set<Class> createAutomatically = []


    CatalogueBuilder(ClassificationService classificationService, ElementService elementService) {
        this.classificationService = classificationService
        this.elementService = elementService
        this.repository = new CatalogueElementProxyRepository(classificationService)
    }

    Set<CatalogueElement> build(@DelegatesTo(CatalogueBuilder) Closure c) {
        reset()
        CatalogueBuilder self = this
        self.with c

        // resolve elements
        for (CatalogueElementProxy element in repository.pendingProxies) {
           created << element.resolve()
        }

        // resolve pending relationships
        for (CatalogueElementProxy element in repository.pendingProxies) {
            element.resolveRelationships()
        }

        created
    }

    CatalogueElementProxy<Classification> classification(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<Classification> classification = createProxy(Classification, parameters)

        context.withNewContext classification, c

        repository.unclassifiedQueriesFor = [MeasurementUnit]

        classification
    }

    CatalogueElementProxy<Model> model(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<Model> model = createProxy(Model, parameters, Classification)

        context.withNewContext model, c

        classifyIfNeeded model

        context.withContextElement(Model) {
            child model
        }

        model
    }

    CatalogueElementProxy<DataElement> dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<DataElement> element = createProxy(DataElement, parameters, Model)

        context.withNewContext element, c

        classifyIfNeeded element

        if (element.getParameter('valueDomain') == null && ValueDomain in createAutomatically) {
            context.withNewContext element, {
                valueDomain()
            }
        }

        context.withContextElement(Model) {
            contains element
        }

        element
    }

    CatalogueElementProxy<ValueDomain> valueDomain(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<ValueDomain> domain = createProxy(ValueDomain, parameters, DataElement)

        context.withNewContext domain, c

        classifyIfNeeded domain

        context.withContextElement(DataElement) {
            it.setParameter('valueDomain', domain)
        }

        if (domain.getParameter('dataType') == null && DataType in createAutomatically) {
            context.withNewContext domain, {
                dataType()
            }
        }
        domain
    }

    CatalogueElementProxy<? extends DataType> dataType(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<? extends DataType> dataType = createProxy((parameters.enumerations ? EnumeratedType : DataType), parameters, ValueDomain)

        context.withNewContext dataType, c

        classifyIfNeeded dataType

        context.withContextElement(ValueDomain) {
            it.setParameter('dataType', dataType)
        }

        dataType
    }

    CatalogueElementProxy<MeasurementUnit> measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<MeasurementUnit> unit = createProxy(MeasurementUnit, parameters)

        context.withNewContext unit, c

        classifyIfNeeded unit

        context.withContextElement(ValueDomain) {
            it.setParameter('unitOfMeasure', unit)
        }

        unit
    }


    void child(String classification, String name) {
        rel "hierarchy" to classification, name
    }

    void child(String name) {
        rel "hierarchy" to name
    }

    void child(CatalogueElementProxy<Model> model) {
        rel "hierarchy" to model
    }

    void contains(String classification, String name) {
        rel "containment" to classification, name
    }

    void contains(String name) {
        rel "containment" to name
    }

    void contains(CatalogueElementProxy<DataElement> element) {
        rel "containment" to element
    }

    void basedOn(String classification, String name) {
        rel "base" from classification, name
    }

    void basedOn(String name) {
        rel "base" from name
    }

    void basedOn(CatalogueElementProxy<CatalogueElement> element) {
        rel "base" from element
    }
    RelationshipBuilder rel(String relationshipTypeName) {
        return new RelationshipBuilder(context, repository, relationshipTypeName)
    }

    void description(String description) { setStringValue('description', description) }
    void rule(String rule)               { setStringValue('rule', rule) }
    void regex(String regex)             { setStringValue('regexDef', regex) }

    /**
     * Sets the id of the current element.
     *
     * This id is not used for queries. If you want to query by model catalogue id, send it as parameter in
     * the domain call such as model(id: 'http://www.example.com/foo').
     * @param id
     * @deprecated use id as input parameter
     */
    @Deprecated
    void id(String id) { setStringValue('modelCatalogueId', id) }

    void status(ElementStatus status) {
        context.withContextElement(CatalogueElement) {
            it.setParameter('status', status)
        } or {
            throw new IllegalStateException("No element to set status on")
        }
    }

    void ext(String key, String value) {
        context.withContextElement(CatalogueElement) {
            it.setExtension(key, value)
        } or {
            throw new IllegalStateException("No element to set ext on")
        }
    }

    void ext(Map<String, String> values) {
        values.each { String key, String value ->
            ext key, value
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
        repository.unclassifiedQueriesFor << type
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




    private <T extends CatalogueElement, A extends CatalogueElementProxy<T>> void classifyIfNeeded(A element) {
        if (Classification.isAssignableFrom(element.domain)) {
            return
        }
        try {
            rel "classification" to element
        } catch (IllegalStateException ignored) {
            // no classification found in context
        }
    }

    private void setStringValue(String name, String value) {
        // XXX: this may cause problem later when someone would like to erase some value
        if (!value) {
            return
        }
        context.withContextElement(CatalogueElement) {
            it.setParameter(name, value?.stripIndent()?.trim())
        } or {
            throw new IllegalStateException("No element to set string value '$name'")
        }
    }

    private void reset() {
        context = new CatalogueBuilderContext()
        created = []

        repository = new CatalogueElementProxyRepository(classificationService)

        createAutomatically = []
    }

    protected <T extends CatalogueElement, A extends CatalogueElementProxy<T>> A createProxy(Class<T> domain, Map<String, Object> parameters, Class inheritFrom = null) {
        if (inheritFrom && domain in SUPPORTED_FOR_AUTO) {
            context.withContextElement(inheritFrom) {
                if (!parameters.name && it.name) {
                    parameters.name = it.name
                }
                if (!parameters.description && it.getParameter('description')) {
                    parameters.description = it.getParameter('description')
                }
            }
        }

        A element = repository.createProxy(domain, parameters) as A

        element.setParameter('name', parameters.name)

        if (parameters.id) {
            element.setParameter('modelCatalogueId', parameters.id)
        }

        parameters.each { String key, Object value ->
            // these are specials handled directly
            if (key in ['id', 'classification', 'name']) {
                return
            }
            element.setParameter(key, value)
        }

        element
    }


    // -- not migrated bellow this line


//    protected <T extends CatalogueElement> T saveIfNeeded(T element) {
//        if (!element) {
//            return element
//        }
//
//        if (element.dirty && !('status' in element.dirtyPropertyNames) && (element.status == ElementStatus.FINALIZED || element.status == ElementStatus.DEPRECATED)) {
//            return createNewVersion(element, element.dirtyPropertyNames as Set<String>)
//        }
//
//        if (element.dirty) {
//            log.info "Persisting changes ${element.dirtyPropertyNames} of $element"
//            return element.save(failOnError: true, flush: true) as T
//        } else if (!element.attached) {
//            log.info "Persisting $element"
//            return element.save(failOnError: true, flush: true) as T
//        }
//
//        return element
//    }

//    protected <T extends CatalogueElement> T createNewVersion(T element, Set<String> changes) {
//        T newVersion = elementService.createDraftVersion(element)
//        for (String propertyName in element.dirtyPropertyNames) {
//            newVersion.setProperty(propertyName, element.getProperty(propertyName))
//        }
//        log.info "Created new version for ${GrailsNameUtils.getNaturalName(element.class.simpleName)} with name ${newVersion.name}. New version differs in ${changes}."
//        element.refresh()
//        return newVersion.save(failOnError: true, flush: true) as T
//    }
//
//
//    private <T extends CatalogueElement> T findOrPrepareInstance(Class<T> type, Map<String, Object> parameters, boolean unclassified = false) {
//        T element
//        if (unclassified) {
//            element = tryFindUnclassified(type, parameters.name, parameters.id)
//        } else if (parameters.classification) {
//            element = tryFind(type, parameters.classification, parameters.name, parameters.id)
//        } else {
//            element = tryFind(type, parameters.name, parameters.id)
//        }
//
//        parameters.remove 'id'
//        parameters.remove 'classification'
//
//        if (!element) {
//            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} does not exist yet, creating new one"
//            element = type.newInstance(parameters)
//        } else {
//            log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} found"
//            Closure propChanged = { String key, Object value ->
//                // if something has changed
//                if (element.hasProperty(key) && element.getProperty(key) != value) {
//                    if (key == 'modelCatalogueKey' && value == null && element.defaultModelCatalogueId == element.modelCatalogueId) {
//                        return false
//                    }
//                    if (key == 'status' && value == null && element.status == ElementStatus.DRAFT) {
//                        return false
//                    }
//                    log.info "${GrailsNameUtils.getNaturalName(type.simpleName)} with name ${parameters.name} has changed. At least $key is different (new value is $value)"
//                    return true
//                }
//                return false
//            }
//            if (parameters.any(propChanged)) {
//                if (element.status == ElementStatus.FINALIZED || element.status == ElementStatus.DEPRECATED) {
//                    Set<String> changed = properties.findAll(propChanged).keySet()
//                    element = createNewVersion(element, changed)
//                }
//                element.properties = parameters
//            }
//
//        }
//
//        created << element
//        element
//    }

}

