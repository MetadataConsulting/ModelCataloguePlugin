package org.modelcatalogue.core.util.builder

import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.modelcatalogue.builder.api.BuilderKeyword
import org.modelcatalogue.builder.api.DataModelPolicyBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.builder.util.AbstractCatalogueBuilder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.api.RelationshipBuilder
import org.modelcatalogue.builder.api.RelationshipConfiguration
import org.modelcatalogue.builder.api.RelationshipTypeBuilder
import org.modelcatalogue.core.policy.Conventions
import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.PolicyBuilder
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.*
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.api.CatalogueElement as ApiCatalogueElement



/**
 * CatalogueBuilder class allows to design the catalogue elements relationship in a tree-like structure simply without
 * having to know the implementation details. CatalogueBuilder handles creating the draft versions if necessary.
 *
 * Practical example how the builder can be used are the imports present in the application or DSL MC files.
 *
 */
@CompileStatic
@Log4j class DefaultCatalogueBuilder extends AbstractCatalogueBuilder {

    /**
     * These classes can be created automatically setting e.g. <code>automatic dataType</code> flag on.
     *
     * @see #automatic(BuilderKeyword)
     */
    private static Set<Class> SUPPORTED_FOR_AUTO = new LinkedHashSet<Class>([DataType, EnumeratedType, ReferenceType, PrimitiveType])

    /**
     * Repository handles fetching the right elements from the database based on id, value or classification.
     */
    private CatalogueElementProxyRepository repository

    /**
     * Keeps the references to currently active elements which will be target of calls to domain language functions.
     */
    private CatalogueBuilderContext context

    /**
     * Set of all catalogue elements created by last call to <code>CatalogueBuilder#build(groovy.lang.Closure)</code> method.
     * The elements don't have to be created by that call but it should be resolved by any of the element creation
     * method such as <code>CatalogueBuilder#model(java.util.Map, groovy.lang.Closure)</code>.
     */
    Set<CatalogueElement> created = []

    /**
     * Set of types to be created automatically.
     *
     * @see #automatic(BuilderKeyword)
     */
    private Set<Class> createAutomatically = []

    /**
     * Top level builder settings to skip dirty checking during the resolution.
     */
    private boolean skipDrafts

    /**
     * If false, all imported relationships will be system.
     */
    private boolean canCreateRelationshipTypes


    ProgressMonitor monitor = ProgressMonitor.NOOP

    /**
     * Creates new catalogue builder with given classification and element services.
     * @param dataModelService classification service
     * @param elementService element service
     */
    DefaultCatalogueBuilder(DataModelService dataModelService, ElementService elementService, boolean canCreateRelationshipTypes = false) {
        this.repository = new CatalogueElementProxyRepository(dataModelService, elementService)
        this.context = new CatalogueBuilderContext(this)
        this.canCreateRelationshipTypes = canCreateRelationshipTypes
    }

    /**
     * Builds catalogue elements based on the DSL method call inside the closure.
     *
     * First it builds the model of catalogue elements in memory than it stores the changes in the database and
     * reset the state of the builder to the default (but the <code>skipDrafts</code> flag remains the same).
     *
     * @param c catalogue definition
     * @return set of resolved elements
     */
    void build(@DelegatesTo(CatalogueBuilder) Closure c) {
        reset()
        DefaultCatalogueBuilder self = this
        self.with c

        created = repository.resolveAllProxies(skipDrafts)

        // we don't want to keep any references in this point
        context.clear()
        repository.clear()
        createAutomatically.clear()

        repository.unclassifiedQueriesFor = new LinkedHashSet<Class>([MeasurementUnit])
    }

    /**
     * Creates new classification, reuses the latest draft or creates new draft unless the exactly same classification
     * already exists in the catalogue. Accepts any bindable parameters which Classification instances does.
     *
     * All elements resolved from DSL method calls inside the closure provided will be classified by this classification
     * automatically. This is the reason why most of the use cases will uses this classification method call as top
     * level call.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     * @return proxy to classification specified by the parameters map and the DSL closure
     */
    void dataModel(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<DataModel> dataModel = createProxy(DataModel, parameters, null, true)

        List<CatalogueElementProxy<DataModel>> proxies = repository.findExistingProxy(DataModel, dataModel.name, dataModel.modelCatalogueId).findAll {
            !CatalogueElementProxyRepository.equals(dataModel, it)
        }

        if(proxies){
            CatalogueElementProxy<DataModel> dataModelLast = proxies.last()
            context.withNewContext dataModel, {
                rel 'supersession' from dataModelLast
            }
        }

        context.withNewContext dataModel, c

        dataModel
    }

    /**
     * Creates new data element, reuses the latest draft or creates new draft unless the exactly same data element
     * already exists in the catalogue. Accepts any bindable parameters which DataElement instances does.
     *
     * Value domain nested inside the DSL definition closure will be set as value domain of this element.
     *
     * If value domains are created automatically a value domain with the same name and description will be created
     * if no nested value domain is specified.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     * @return proxy to data element specified by the parameters map and the DSL closure
     */
    void dataElement(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<DataElement> element = createProxy(DataElement, parameters, DataClass, isUnderControlIfSameClassification(parameters))

        context.withNewContext element, c

        if (element.getParameter('dataType') == null && DataType in createAutomatically) {
            context.withNewContext element, {
                dataType()
            }
        }

        context.withContextElement(DataClass) { ignored, Closure relConf ->
            contains element, relConf
        }

        context.withContextElement(ValidationRule) { ignored, Closure relConf ->
            rel 'involvedness' to element, relConf
        }

        element
    }



    /**
     * Creates new model, reuses the latest draft or creates new draft unless the exactly same model already exists
     * in the catalogue. Accepts any bindable parameters which Model instances does.
     *
     * Models nested inside the DSL definition closure will be set as child models for this model.
     * Data elements nested inside the DSL definition closure will be set as contained elements for this model.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     * @return proxy to model specified by the parameters map and the DSL closure
     */
    void dataClass(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<DataClass> dataClass = createProxy(DataClass, parameters, DataModel, isUnderControlIfSameClassification(parameters))

        context.withNewContext dataClass, c
        context.withContextElement(DataType) {
            it.setParameter('dataClass', dataClass)
        }
        context.withContextElement(DataClass) { ignored, Closure relConf ->
            child dataClass, relConf
        }

        dataClass
    }

    /**
     * Creates new validation rule, reuses the latest draft or creates new draft unless the exactly same validation rule
     * already exists in the catalogue. Accepts any bindable parameters which valiadtion rule instances does.
     *
     * Models nested inside the DSL definition closure will be set as child models for this model.
     * Data elements nested inside the DSL definition closure will be set as contained elements for this model.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     */
    void validationRule(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<ValidationRule> validationRule = createProxy(ValidationRule, parameters, null, isUnderControlIfSameClassification(parameters))

        context.withNewContext validationRule, c
        context.withContextElement(DataClass) { ignored, Closure relConf ->
            rel 'ruleContext' from validationRule, relConf
        }

        validationRule
    }

    /**
     * Creates new tag, reuses the latest draft or creates new draft unless the exactly same tag
     * already exists in the catalogue. Accepts any bindable parameters which tag instances does.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     */
    void tag(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<Tag> tag = createProxy(Tag, parameters, null, isUnderControlIfSameClassification(parameters))

        context.withNewContext tag, c


        context.withContextElement(DataElement) { ignored, Closure relConf ->
            rel 'tag' from tag, relConf
        }

        tag
    }

    /**
     * Creates new data type, reuses the latest draft or creates new draft unless the exactly same data type
     * already exists in the catalogue. Accepts any bindable parameters which DataType instances does.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     * @return proxy to data type specified by the parameters map and the DSL closure
     */
    void dataType(Map<String, Object> parameters = [:], @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        Class type = (parameters.enumerations != null ? EnumeratedType : DataType)
        if (parameters.containsKey('enumerations') && !parameters.enumerations) {
            parameters.remove('enumerations')
        }

        context.withContextElement(DataType) {
            // this is here to simplify importing legacy 1.x XML
            if (!parameters.id && !parameters.name) {
                parameters.name = it.getParameter('name')
            }
        }


        CatalogueElementProxy<? extends DataType> dataType = createDataTypeProxy(type, parameters)

        context.withNewContext dataType, c

        if (dataType.getParameter('dataClass')) {
            if (dataType instanceof DefaultCatalogueElementProxy) {
                DefaultCatalogueElementProxy proxy = (DefaultCatalogueElementProxy) dataType;
                proxy.domain = ReferenceType
            }
        }
        if (dataType.getParameter('measurementUnit')) {
            if (dataType instanceof DefaultCatalogueElementProxy) {
                DefaultCatalogueElementProxy proxy = (DefaultCatalogueElementProxy) dataType;
                proxy.domain = PrimitiveType
            }
        }

        context.withContextElement(DataElement) {
            it.setParameter('dataType', dataType)
        }

        context.withContextElement(DataType) { CatalogueElementProxy outerDataType, Closure relConf ->
            if (outerDataType.name == dataType.name) {
                dataType.merge(outerDataType)
            } else {
                if(outerDataType.domain!=dataType.domain) {
                    if(outerDataType instanceof DefaultCatalogueElementProxy) {
                        DefaultCatalogueElementProxy proxy = (DefaultCatalogueElementProxy) outerDataType;
                        proxy.domain = dataType.domain
                    }
                }
                basedOn dataType, relConf
            }
        }

        dataType
    }

    @CompileDynamic
    private CatalogueElementProxy<? extends DataType> createDataTypeProxy(Class<? extends DataType> type, Map<String, Object> parameters) {
        createProxy(type, parameters, DataElement, isUnderControlIfSameClassification(parameters))
    }

    /**
     * Creates new measurement unit, reuses the latest draft or creates new draft unless the exactly same measurement
     * unit already exists in the catalogue. Accepts any bindable parameters which MeasurementUnit instances does.
     *
     * @param parameters map of parameters such as name or id
     * @param c DSL definition closure
     * @return proxy to measurement unit specified by the parameters map and the DSL closure
     */
    void measurementUnit(Map<String, Object> parameters, @DelegatesTo(CatalogueBuilder) Closure c = {}) {
        CatalogueElementProxy<MeasurementUnit> unit = createProxy(MeasurementUnit, parameters, null, isUnderControlIfSameClassification(parameters))

        context.withNewContext unit, c

        context.withContextElement(DataType) {
            it.setParameter('measurementUnit', unit)
        }

        unit
    }

    /**
     * Configures the relationships created automatically for nested elements such as the containment relationship
     * for data element nested in model.
     *
     * Primary use case for this method call is to configure the relationship metadata such as "Min. Occurs".
     *
     * @param relationshipExtensionsConfiguration DSL definition closure expecting setting the relationship metadata
     * @see RelationshipConfiguration
     */
    void relationship(@DelegatesTo(RelationshipConfiguration) Closure relationshipExtensionsConfiguration) {
        context.configureCurrentRelationship(relationshipExtensionsConfiguration)
    }

    /**
     * Adds the base element specified by given classification and name to a parent element.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param classification classification of the base element
     * @param name name of the base element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see RelationshipConfiguration
     */
    void basedOn(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        context.withContextElement(CatalogueElement) {
            rel "base" to ModelCatalogueTypes.getType(it.domain) called classification, name, extensions
        }
    }

    /**
     * Adds the base element specified by given name to the parent element. The model is searched within the parent
     * classification if not global search for the particular element set.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param name name of the base element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void basedOn(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        context.withContextElement(CatalogueElement) {
            rel "base" to ModelCatalogueTypes.getType(it.domain) called name, extensions
        }
    }

    /**
     * Adds the base element specified by given proxy to the parent element.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param model proxy of the base element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void basedOn(ApiCatalogueElement element, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "base" to element, extensions
    }

    /**
     * Allows to create a catalogue element proxy only from given ID.
     * @param id ID of the target of the proxy created
     * @return proxy specified by given ID
     */
    ApiCatalogueElement ref(String id) {
        repository.createProxy(CatalogueElement, [id: id as Object])
    }

    /**
     * Creates new relationship builder for given relationship type specified by name.
     * @param relationshipTypeName name of the relationship type
     * @return the builder for given relationship type
     * @see RelationshipBuilder
     */
    RelationshipBuilder rel(String relationshipTypeName) {
        if (relationshipTypeName == 'classification' || relationshipTypeName == 'declaration') {
            return new SetDataModelBuilder(context, repository)
        }
        return new DefaultRelationshipBuilder(context, repository, relationshipTypeName)
    }


    /**
     * Sets the status of the current element. Currently it does not work as expected as it sets the status property
     * right after object is resolved instead e.g. finalizing the element after all the work is done.
     * @param status new status of the element
     */
    @Deprecated
    void status(ElementStatus status) {
        context.withContextElement(CatalogueElement) {
            it.setParameter('status', status)
        } or {
            throw new IllegalStateException("No element to set status on")
        }
    }

    /**
     * Sets the extension (metadata) for current element from given key and value pair.
     *
     * For setting the extensions (metadata) of relationship between current element and its parent element
     * use #relationship(Closure).
     *
     * @param key metadata key
     * @param value metadata value
     */
    void ext(String key, String value) {
        context.withContextElement(CatalogueElement) {
            it.setExtension(key, value)
        } or {
            throw new IllegalStateException("No element to set ext on")
        }
    }

    /**
     * Disables the dirty-checking during the builder calls for faster imports when it's known that all the changes
     * are desired (i.e. importing into an empty catalogue).
     * @param draft must be "draft" or PublishingStatus#DRAFT
     */
    void skip(ElementStatus draft) {
        if (draft == ElementStatus.DRAFT) {
            skipDrafts = true
            return
        }
        throw new IllegalArgumentException("Only 'draft' is expected after 'skip' keyword")
    }

    /**
     * Sets the flag to copy relationships when draft element is created (e.g. there is an update for element).
     *
     * Normally the relationships are skipped when creating draft and the builder is supposed to rebuilt them
     * (for example in XML or MC files) but sometimes the it's desired to copy the relationships as well (e.g.
     * Excel import which usually provides incomplete data).
     *
     * @param relationships must be "relationships" string (#getRelationships() shortcut can be used)
     */
    void copy(String relationships) {
        if (relationships == getRelationships()) {
            repository.copyRelationships()
            return
        }
        throw new IllegalArgumentException("Only 'relationships' is expected after 'copy' keyword")
    }


    /**
     * Sets the flag to be able to search for elements having no classification at all even the classification is
     * required in other situations.
     *
     * This is mainly for fixing the classifications in older database. Let's say you have a value domain "speed"
     * without any classification and you would like to be matched by name inside the "Car" classification than you will
     * use <code>globalSearchFor measurementUnit</code> declaration nested inside that "Car" classification so if the
     * "speed" domain is not found within "Car" classification but if there is a "speed" domain without any
     * classification that domain will be matched.
     *
     * This setting only applies within one classification DSL closure.
     *
     * Measurement units has the unclassified search enabled by default.
     *
     * @param type type for unclassified searches
     */
    void globalSearchFor(BuilderKeyword type){
        if (type instanceof ModelCatalogueTypes) {
            repository.unclassifiedQueriesFor << type.implementation
        } else {
            throw new IllegalArgumentException("Unsupported keyword: $type")
        }
    }

    /**
     * Trigger the automatic creation of nested elements of given types. Currently data types and value domains are
     * supported for automatic creation.
     *
     * If for example automatic creation of value domains is set and there is no nested <code>dataType</code> call
     * inside <code>dataElement</code> DSL definition closure value domain is created or matched with the name
     * of the data element and its description. Use this feature in imports when imported data don't have a concept
     * of value domains or data types to create value domains and data types placeholders automatically.
     *
     * You can set both supported classes calling this method twice.
     *
     * @param currently only dataType is supported
     */
    void automatic(BuilderKeyword type){
        if (type instanceof ModelCatalogueTypes) {
            if (!(type.implementation in SUPPORTED_FOR_AUTO)) {
                throw new IllegalArgumentException("Only supported values are ${SUPPORTED_FOR_AUTO.collect{GrailsNameUtils.getPropertyName(it)}.join(', ')}")
            }
            createAutomatically << type.implementation
        } else {
            throw new IllegalArgumentException("Unsupported keyword: $type")
        }
    }

    @Override
    void relationshipType(Map<String, Object> map, @DelegatesTo(RelationshipTypeBuilder) Closure closure) {
        RelationshipType type = RelationshipType.readByName(map.name?.toString())
        if (type) {
            return
        }

        type = new RelationshipType(name: map.name)
        type.system = canCreateRelationshipTypes ? map.system : true
        type.bidirectional = map.bidirectional
        type.versionSpecific = map.versionSpecific
        type.sourceClass = Class.forName(map.source?.toString())
        type.destinationClass = Class.forName(map.destination?.toString())

        DefaultRelationshipTypeBuilder relationshipTypeBuilder = new DefaultRelationshipTypeBuilder(type)
        relationshipTypeBuilder.with closure

        FriendlyErrors.failFriendlySave(type)
    }
/**
     * Adds classifications to given element. It uses the deepest parent classification.
     * @param element element to be classified
     */
    private <T extends CatalogueElement, A extends CatalogueElementProxy<T>> void classifyIfNeeded(A element) {
        if (DataModel.isAssignableFrom(element.domain)) {
            return
        }

        if (element.classification) {
            return
        }

        if (element.modelCatalogueId && !element.name) {
            // ref
            return
        }

        context.withContextElement(DataModel, true) {
            element.setParameter('dataModel', it)
        }
    }



    /**
     * Helper method to set the sting value the the parent element.
     *
     * If the value is null or empty string nothing happens.
     *
     * @param name name of the property
     * @param value value of the propery
     */
    protected void setStringValue(String name, String value) {
        // XXX: this may cause problem later when someone would like to erase some value
        // this is here because of generated COSD MC file was sending empty strings even for the values which
        // does not accept them
        if (!value) {
            return
        }
        context.withContextElement(CatalogueElement) {
            it.setParameter(name, value?.stripIndent()?.trim())
        } or {
            throw new IllegalStateException("No element to set string value '$name'")
        }
    }

    /**
     * Resets the builder.
     *
     * This is called as the first statement inside the #build(Closure) method resetting builder to defaults.
     */
    private void reset() {
        context.clear()
        repository.clear()
        repository.monitor = monitor
        createAutomatically.clear()
        created.clear()
    }

    /**
     * Creates the proxy for given configuration.
     *
     * If a type is supported for automatic creation (data type and value domain at the moment) it also handles
     * inheriting the name and description for every nested dataType call.
     *
     * @param domain the class of the resolved element
     * @param parameters initial parameters mapp
     * @param inheritFrom class from which the name and description should be inherited
     * @return proxy for given configuration.
     */
    protected <T extends CatalogueElement, A extends CatalogueElementProxy<T>> A createProxy(Class<T> domain, Map<String, Object> parameters, Class inheritFrom = null, boolean underControl = false) {
        if (inheritFrom && domain in SUPPORTED_FOR_AUTO) {
            context.withContextElement(inheritFrom) {
                if (!parameters.id && !parameters.name) {
                    if (it.name) {
                        parameters.name = it.name
                        parameters[CatalogueElementProxyRepository.AUTOMATIC_NAME_FLAG] = true
                    }
                    // description is only transffered for the elements created automatically
                    if (!parameters.id && parameters.name && domain in createAutomatically && !parameters.description && it.getParameter('description')) {
                        parameters[CatalogueElementProxyRepository.AUTOMATIC_DESCRIPTION_FLAG] = true
                        parameters.description = it.getParameter('description')
                    }
                }
            }
        }

        A element = repository.createProxy(domain, parameters, underControl) as A

        element.setParameter('name', parameters.name)

        if (parameters.id) {
            element.setParameter('modelCatalogueId', parameters.id)
        } else if(idBuilder != null) {
            element.setParameter('modelCatalogueId', getIdFromIdBuilder(element, domain))
        }

        if (parameters.classification || parameters.dataModel) {
            element.setParameter('dataModel', createProxy(DataModel, [name: parameters.dataModel ?: parameters.classification]))
        }

        parameters.each { String key, Object value ->
            // these are specials handled directly
            if (key in ['id', 'classification', 'dataModel',  'name']) {
                return
            }
            element.setParameter(key, value)
        }

        classifyIfNeeded element

        element
    }

    @Override
    void policy(String policy) {
        if (!policy) {
            return
        }
        context.withContextElement(CatalogueElement) {
            it.addToPendingPolicies(policy)
        } or {
            throw new IllegalStateException("No element to set string value '$policy'")
        }
    }

    // I wonder if this should be @Transactional...
    @Override @CompileDynamic @Transactional
    void dataModelPolicy(Map<String, Object> parameters, @DelegatesTo(DataModelPolicyBuilder.class) Closure configuration) {
        DataModelPolicy policy = DataModelPolicy.findByName(parameters.name?.toString())
        if (policy && !parameters.overwrite) { // return (do nothing) if policy already exists and we're not overwriting
            return
        }

        PolicyBuilder builder = PolicyBuilder.create()
        new DefaultDataModelPolicyBuilder(builder).with configuration
        Policy builtPolicy = builder.createPolicy()
        if (policy && parameters.overwrite) {
            policy.policyText = builtPolicy.toString()
            FriendlyErrors.failFriendlySave(policy)
        }
        else {
            FriendlyErrors.failFriendlySave(new DataModelPolicy(name: parameters.name?.toString(), policyText: builtPolicy.toString()))

        }
    }

    @CompileDynamic
    private String getIdFromIdBuilder(ApiCatalogueElement element, Class domain) {
        idBuilder(element.name, domain)
    }

    private boolean isUnderControlIfSameClassification(Map<String, Object> parameters) {
        if (!parameters.dataModel && !parameters.classification) {
            return true
        }
        boolean ret = true
        context.withContextElement(DataModel, true) {
            ret = it.name == (parameters.dataModel ?: parameters.classification)?.toString()
        }
        return ret
    }
}

