package org.modelcatalogue.builder.util

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.modelcatalogue.builder.api.*
import org.modelcatalogue.core.api.CatalogueElement
import org.modelcatalogue.core.api.ElementStatus

abstract class AbstractCatalogueBuilder implements CatalogueBuilder {

    /**
     * Closure for assigning ids based on their names
     */
    protected Closure<String> idBuilder

    /**
     * Adds the model specified by given classification and name to a parent model.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param classification classification of the child model
     * @param name name of the child model
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     */
    void child(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "hierarchy" to classification, name, extensions
    }

    /**
     * Adds the model specified by given name to the parent model. The model is searched within the parent
     * classification if not global search for models set.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param name name of the child model
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void child(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "hierarchy" to name, extensions
    }

    /**
     * Adds the model specified by given proxy to the parent model.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param model proxy of the child model
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void child(CatalogueElement model, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "hierarchy" to model, extensions
    }

    /**
     * Adds the data element specified by given classification and name to a parent model.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param classification classification of the contained data element
     * @param name name of the contained data element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     */
    void contains(String classification, String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "containment" to classification, name, extensions
    }

    /**
     * Adds the data element specified by given name to the parent model. The data element is searched within the parent
     * classification if not global search for data elements set.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param name name of the contained data element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void contains(String name, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "containment" to name, extensions
    }

    /**
     * Adds the data element specified by given proxy to the parent model.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param model proxy of the contained data element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void contains(CatalogueElement element, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "containment" to element, extensions
    }


    /**
     * Adds the base element specified by given proxy to the parent element.
     *
     * Metadata for this hierarchy relationship can be set using the extensions DSL definition closure.
     *
     * @param model proxy of the base element
     * @param extensions DSL definition closure expecting setting the relationship metadata
     * @see org.modelcatalogue.builder.api.RelationshipConfiguration
     * @see #globalSearchFor(BuilderKeyword)
     */
    void basedOn(CatalogueElement element, @DelegatesTo(RelationshipConfiguration) Closure extensions = {}) {
        rel "base" from element, extensions
    }

    /**
     * Assigns the id of the element dynamically.
     *
     * The builder closure should take two parameters a String name and Class type and transform them into String id
     * which must be valid URL.
     *
     * @param idBuilder builder closure
     */
    void id(@DelegatesTo(CatalogueBuilder) @ClosureParams(value=FromString, options=['String,Class']) Closure<String> idBuilder) {
        this.idBuilder = idBuilder
    }


    /**
     * Sets the description of element.
     * @param description description of element
     */
    void description(String description) { setStringValue('description', description) }

    /**
     * Sets the rule of the value domain. Fails if not inside value domain definition or any other catalogue element
     * having the rule property.
     * @param rule rule of the parent value domain
     */
    void rule(String rule)               { setStringValue('rule', rule) }

    /**
     * Sets the regular expression rule of the value domain. Fails if the current is not a value domain or any other
     * catalogue element supporting setting the regular expression rule property.
     * @param rule rule of the parent value domain
     */
    void regex(String regex)             { setStringValue('regexDef', regex) }

    /**
     * Sets the model catalogue id of the current element. The id must be a valid URL.
     * @param id id which must be valid URL
     * @see #id(groovy.lang.Closure)
     */
    void id(String id) {
        setStringValue('modelCatalogueId', id)
    }

    /**
     * Keyword to be used with #copy(String) method.
     * @return string "relationships"
     */
    String getRelationships() { "relationships" }


    /**
     * Helper method to set the sting value the the parent element.
     *
     * If the value is null or empty string nothing happens.
     *
     * @param name name of the property
     * @param value value of the propery
     */
    abstract protected void setStringValue(String name, String value)

    @Override
    void ext(Map<String, String> values) {
        for (Map.Entry<String, String> entry in values.entrySet()) {
            ext entry.key, entry.value
        }
    }

    @Override
    ElementStatus getFinalized() {
        ElementStatus.FINALIZED
    }

    @Override
    ElementStatus getDeprecated() {
        ElementStatus.DEPRECATED
    }

    @Override
    ElementStatus getDraft() {
        ElementStatus.DRAFT
    }

    @Override
    BuilderKeyword getClassification() {
        return ModelCatalogueTypes.CLASSIFICATION
    }

    @Override
    BuilderKeyword getModel() {
        return ModelCatalogueTypes.MODEL
    }

    @Override
    BuilderKeyword getDataElement() {
        return ModelCatalogueTypes.DATA_ELEMENT
    }

    @Override
    BuilderKeyword getValueDomain() {
        return ModelCatalogueTypes.VALUE_DOMAIN
    }

    @Override
    BuilderKeyword getDataType() {
        return ModelCatalogueTypes.DATA_TYPE
    }

    @Override
    BuilderKeyword getMeasurementUnit() {
        return ModelCatalogueTypes.MEASUREMENT_UNIT
    }
}

