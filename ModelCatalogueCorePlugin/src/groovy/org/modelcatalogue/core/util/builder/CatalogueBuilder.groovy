package org.modelcatalogue.core.util.builder

import grails.util.GrailsNameUtils
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*

@Log4j
class CatalogueBuilder implements ExtensionAwareBuilder {

    private static Set<Class> SUPPORTED_FOR_AUTO    = [DataType, EnumeratedType, ValueDomain]

    private CatalogueElementProxyRepository repository
    private CatalogueBuilderContext context

    private Set<CatalogueElement> created = []
    private Set<Class> createAutomatically = []

    private boolean skipDrafts


    CatalogueBuilder(ClassificationService classificationService, ElementService elementService) {
        this.repository = new CatalogueElementProxyRepository(classificationService, elementService)
        this.context = new CatalogueBuilderContext(this)
    }

    Set<CatalogueElement> build(@DelegatesTo(CatalogueBuilder) Closure c) {
        reset()
        CatalogueBuilder self = this
        self.with c

        created = repository.resolveAllProxies(skipDrafts)

        // we don't want to keep any references in this point
        context.clear()
        repository.clear()
        createAutomatically.clear()

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
        context.withContextElement(Model) { ignored, Closure relConf ->
            child model, relConf
        }

        model
    }

    CatalogueElementProxy<DataElement> dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<DataElement> element = createProxy(DataElement, parameters, Model)

        context.withNewContext element, c

        if (element.getParameter('valueDomain') == null && ValueDomain in createAutomatically) {
            context.withNewContext element, {
                valueDomain()
            }
        }

        context.withContextElement(Model) { ignored, Closure relConf ->
            contains element, relConf
        }

        element
    }

    CatalogueElementProxy<ValueDomain> valueDomain(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<ValueDomain> domain = createProxy(ValueDomain, parameters, DataElement)

        context.withNewContext domain, c

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
        Class type = (parameters.enumerations ? EnumeratedType : DataType)
        if (parameters.containsKey('enumerations') && !parameters.enumerations) {
            parameters.remove('enumerations')
        }
        CatalogueElementProxy<? extends DataType> dataType = createProxy(type, parameters, ValueDomain)

        context.withNewContext dataType, c

        context.withContextElement(ValueDomain) {
            it.setParameter('dataType', dataType)
        }

        dataType
    }

    CatalogueElementProxy<MeasurementUnit> measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<MeasurementUnit> unit = createProxy(MeasurementUnit, parameters)

        context.withNewContext unit, c

        context.withContextElement(ValueDomain) {
            it.setParameter('unitOfMeasure', unit)
        }

        unit
    }

    void relationship(@DelegatesTo(ExtensionAwareBuilder) Closure relationshipExtensionsConfiguration) {
        context.configureCurrentRelationship(relationshipExtensionsConfiguration)
    }


    void child(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "hierarchy" to classification, name, extensions
    }

    void child(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "hierarchy" to name, extensions
    }

    void child(CatalogueElementProxy<Model> model, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "hierarchy" to model, extensions
    }

    void contains(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "containment" to classification, name, extensions
    }

    void contains(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "containment" to name, extensions
    }

    void contains(CatalogueElementProxy<DataElement> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "containment" to element, extensions
    }

    void basedOn(String classification, String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(CatalogueElement) {
            rel "base" from it.domain called classification, name, extensions
        }
    }

    void basedOn(String name, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        context.withContextElement(CatalogueElement) {
            rel "base" from it.domain called name, extensions
        }
    }

    void basedOn(CatalogueElementProxy<CatalogueElement> element, @DelegatesTo(ExtensionAwareBuilder) Closure extensions = {}) {
        rel "base" from element, extensions
    }

    void id(@DelegatesTo(CatalogueBuilder) @ClosureParams(value=FromString, options=['String,Class']) Closure<String> idBuilder) {
        context.withContextElement(CatalogueElement) {
            String name = it.getParameter('name')
            if (!name) {
                throw new IllegalStateException("Missing name of $it")
            }
            it.setId(idBuilder(name, it.domain))
        } or {
            throw new IllegalStateException("No element to set id on")
        }
    }

    CatalogueElementProxy<? extends CatalogueElement> ref(String id) {
        repository.createAbstractionById(CatalogueElement, null, id)
    }

    RelationshipBuilder rel(String relationshipTypeName) {
        return new RelationshipBuilder(context, repository, relationshipTypeName)
    }

    void description(String description) { setStringValue('description', description) }
    void rule(String rule)               { setStringValue('rule', rule) }
    void regex(String regex)             { setStringValue('regexDef', regex) }

    void id(String id) {
        setStringValue('modelCatalogueId', id)
    }

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

    void skip(ElementStatus draft) {
        if (draft == ElementStatus.DRAFT) {
            skipDrafts = true
            return
        }
        throw new IllegalArgumentException("Only 'draft' is expected after 'skip' keyword")
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


    private <T extends CatalogueElement, A extends CatalogueElementProxy<T>> void classifyIfNeeded(A element) {
        if (Classification.isAssignableFrom(element.domain)) {
            return
        }
        context.withContextElement(Classification) {
            element.addToPendingRelationships(new RelationshipProxy('classification', it, element, [:]))
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
        context.clear()
        repository.clear()
        createAutomatically.clear()

        created.clear()

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

        classifyIfNeeded element

        element
    }

}

