package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain

@Log4j
class DefaultCatalogueElementProxy<T extends CatalogueElement> implements CatalogueElementProxy<T> {

    static final List<Class> KNOWN_DOMAIN_CLASSES = [Asset, CatalogueElement, Classification, DataElement, DataType, EnumeratedType, MeasurementUnit, Model, ValueDomain]

    Class<T> domain

    String id
    String name
    String classification

    protected CatalogueElementProxyRepository repository

    private final Map<String, Object> parameters = [:]
    private final Map<String, String> extensions = [:]
    private final Set<RelationshipProxy> relationships = []

    private CatalogueElementProxy<T> replacedBy
    private T resolved

    DefaultCatalogueElementProxy(CatalogueElementProxyRepository repository, Class<T> domain, String id, String classification, String name) {
        if (!(domain in KNOWN_DOMAIN_CLASSES)) {
            throw new IllegalArgumentException("Only domain classes of $KNOWN_DOMAIN_CLASSES are supported as proxies")
        }

        this.repository = repository
        this.domain = domain

        this.id = id
        this.name = name
        this.classification = classification
    }

    @Override
    final T resolve() {
        if (replacedBy) {
            return replacedBy.resolve()
        }

        if(resolved) {
            return resolved
        }

        resolved = fill(findExisting())

        if (resolved) {
            return resolved
        }

        log.debug "$this not found, creating new one"

        resolved = fill(domain.newInstance())

        resolved
    }

    @Override
    Object getParameter(String key) {
        parameters.get(key)
    }

    @Override
    void setParameter(String key, Object value) {
        if (resolved) {
            throw new IllegalStateException("This catalogue element is already resolved!")
        }

        if (key == 'modelCatalogueId') {
            id = value?.toString()
        }

        if (key == 'name') {
            name = value?.toString()
        }

        parameters.put(key, value)
    }

    @Override
    void setExtension(String key, String value) {
        if (resolved) {
            throw new IllegalStateException("This catalogue element is already resolved!")
        }
        extensions.put(key, value)
    }

    T requestDraft() {
        T existing = findExisting()

        if (!existing) {
            return null
        }

        if (existing.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED]) {
            return repository.createDraftVersion(existing)
        }
        return existing
    }

    private boolean isParametersChanged(T element) {
        parameters.any { String key, Object value ->
            def realValue = value instanceof CatalogueElementProxy ? value.resolve() : value
            element.getProperty(key) != realValue
        }
    }

    private boolean isExtensionsChanged(T element) {
        extensions.any { String key, String value ->
            element.ext.get(key) != value
        }
    }

    T findExisting() {
        if (id) {
            return repository.findById(domain, id)
        }
        if (name) {
            if (classification) {
                return repository.tryFind(domain, classification, name, id)
            }
            return repository.tryFindUnclassified(domain, name, id)
        }
        throw new IllegalStateException("Missing id, classification and name so there is no way how to find existing element")
    }

    private T fill(T element) {
        if (!element) {
            return element
        }
        boolean changed =  isParametersChanged(element)

        if (!changed) {
            boolean extChanged = isExtensionsChanged(element)

            if (!extChanged) {
                log.debug "$this has no changes"
                return element
            }

            log.debug "$this has different in metadata"

            element = createNewVersionIfNeeded(element)

            updateExtensions(element)

            return element
        }

        element = createNewVersionIfNeeded(element)

        parameters.each { String key, Object value ->
            def realValue = value instanceof CatalogueElementProxy ? value.resolve() : value
            if (element.getProperty(key) != realValue) {
                log.debug "$this has changed - property $key is now $realValue"
                element.setProperty(key, realValue)
            }
        }

        log.debug "Saving $this"
        repository.save(element)

        updateExtensions(element)

        element
    }

    private <T extends CatalogueElement> void updateExtensions(T element) {
        extensions.each { String key, String value ->
            if (element.ext.get(key) != value) {
                log.debug "$this has changed - extension $key is now $value"
                element.ext.put(key, value)
            }
        }
    }

    private <T extends CatalogueElement> T createNewVersionIfNeeded(T element) {
        if (element.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED]) {
            log.debug "$this is finalized, creating new draft version"
            return repository.createDraftVersion(element)
        }
        element
    }

    @Override
    void addToPendingRelationships(RelationshipProxy relationshipProxy) {
        if (!classification && relationshipProxy.relationshipTypeName == 'classification' && repository.equals(this, relationshipProxy.destination)) {
            classification = relationshipProxy.source.name
        }
        relationships << relationshipProxy
    }

    @Override
    Set<Relationship> resolveRelationships() {
        relationships.collect {
            it.resolve()
        }
    }

    String toString() {
        "Proxy of $domain[id: $id, classification: $classification, name: $name]"
    }

    @Override
    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other) {
        if (!(other instanceof DefaultCatalogueElementProxy) ) {
            throw new IllegalArgumentException("Can only merge with other default catalogue element proxies")
        }

        other.extensions.each { String key, String value ->
            setExtension(key, value)
        }

        other.parameters.each { String key, Object value ->
            setParameter(key, value)
        }

        other.relationships.each { RelationshipProxy relationship ->
            if (repository.equals(this, relationship.source)) {
                addToPendingRelationships(new RelationshipProxy(relationship.relationshipTypeName, this, relationship.destination))
            }

            if (repository.equals(this, relationship.destination)) {
                addToPendingRelationships(new RelationshipProxy(relationship.relationshipTypeName, relationship.source, this))
            }
        }

        other.replacedBy = this

        this
    }
}
