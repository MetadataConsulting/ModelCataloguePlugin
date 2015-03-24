package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.*

@Log4j class DefaultCatalogueElementProxy<T extends CatalogueElement> implements CatalogueElementProxy<T> {

    static final List<Class> KNOWN_DOMAIN_CLASSES = [Asset, CatalogueElement, Classification, DataElement, DataType, EnumeratedType, MeasurementUnit, Model, ValueDomain]

    Class<T> domain

    String id
    String name
    String classification

    boolean newlyCreated

    protected CatalogueElementProxyRepository repository

    private final Map<String, Object> parameters = [:]
    private final Map<String, String> extensions = [:]
    final Set<RelationshipProxy> relationships = []

    private CatalogueElementProxy<T> replacedBy
    private T resolved
    private String draftRequest
    private String changed

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

    Set<RelationshipProxy> getPendingRelationships() {
        relationships
    }

    boolean isNew() {
        if (replacedBy) {
            return replacedBy.isNew()
        }
        return newlyCreated
    }

    @Override
    final T resolve() {
        try {
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

            newlyCreated = true
            resolved = fill(domain.newInstance())

            return resolved
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve $this:\n\n$e", e)
        }

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

    void requestDraft() {
        draftRequest = changed == null ? (changed = getChanged()) : changed
    }

    T createDraftIfRequested() {
        if (!draftRequest) {
            return null
        }

        T existing = findExisting()

        if (!existing) {
            return null
        }

        if (existing.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED]) {
            log.info("New draft version created for $this. Reason: $draftRequest")
            return repository.createDraftVersion(existing)
        }
        return existing
    }

    private boolean isParametersChanged(T element) {
        parameters.any { String key, Object value ->
            def currentValue = element.getProperty(key)
            if (value instanceof CatalogueElementProxy) {
                def realValue = value.findExisting()
                if (realValue?.latestVersionId && realValue?.latestVersionId != currentValue?.latestVersionId) {
                    log.debug "$this has changed at least one property - property $key\n\n===NEW===\n$realValue\n===OLD===\n${currentValue}\n========="
                    return true
                }
                if (realValue?.id != currentValue?.id) {
                    log.debug "$this has changed at least one property - property $key\n\n===NEW===\n$realValue\n===OLD===\n${currentValue}\n========="
                    return true
                }
                return false
            }
            if (normalizeWhitespace(currentValue) != normalizeWhitespace(value)) {
                if (key == 'modelCatalogueId' && value?.toString()?.startsWith(element.getDefaultModelCatalogueId(true))) {
                    return false
                }
                log.debug "$this has changed at least one property - property $key\n\n===NEW===\n${normalizeWhitespace(value)}\n===OLD===\n${normalizeWhitespace(currentValue)}\n========="
                return true
            }
            return false
        }
    }

    private boolean isExtensionsChanged(T element) {
        extensions.any { String key, String value ->
            boolean result = normalizeWhitespace(element.ext.get(key)) != normalizeWhitespace(value)
            if (result) {
                log.debug "$this has changed at least one extension - extension $key\n\n===NEW===\n${normalizeWhitespace(value)}\n===OLD===\n${normalizeWhitespace(element.ext.get(key))}\n========="
            }
            result
        }
    }

    String getChanged() {
        try {
            T existing = findExisting()
            if (!existing) {
                return changed = "Does Not Exist Yet"
            }

            if (isParametersChanged(existing)) {
                return changed = "Parameters Changed"
            }

            if (isExtensionsChanged(existing)) {
                return changed = "Extensions Changed"
            }

            if (isRelationshipsChanged()) {
                return changed = "Relationship Changed"
            }

            return ""
        } catch (e) {
            throw new IllegalStateException("Error while determining whether $this changed!", e)
        }
    }

    boolean isRelationshipsChanged() {
        relationships.any {
            CatalogueElement source = it.source.findExisting()

            if (!source) return true

            CatalogueElement destination = it.destination.findExisting()

            if (!destination) return true

            RelationshipType type = RelationshipType.readByName(it.relationshipTypeName)

            if (!type) return true

            Relationship found = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, type)

            if (!found) {
                log.debug "$this has changed at least one relationship $it"
                return true
            }


            if (it.extensions != found.ext) {
                log.debug "$this has changed at least one relationship $it. it has changed metadata. old: ${found.ext}, new: ${it.extensions}"
                return true
            }

            return false
        }
    }

    T findExisting() {
        if (id) {
            T result = repository.findById(domain, id)
            if (result) {
                return result
            }
            if (!name) {
                throw new IllegalStateException("Missing id, classification and name so there is no way how to find existing element")
            }
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

        if (changed == null) {
            changed = getChanged()
        }

        if (!changed) {
            return element
        }

        updateProperties(element)

        log.debug "Saving properties of $this"
        repository.save(element)

        updateExtensions(element)

        element
    }

    private Map<String, Object> updateProperties(T element) {
        element.name = name
        if (id && !id.startsWith(element.getDefaultModelCatalogueId(true))) {
            element.modelCatalogueId = id
        }
        parameters.each { String key, Object value ->
            if (value instanceof CatalogueElementProxy) {
                element.setProperty(key, value.resolve())
                return
            }
            if (value instanceof String) {
                element.setProperty(key, value.trim())
            }
            element.setProperty(key, value)
        }
    }

    private <T extends CatalogueElement> void updateExtensions(T element) {
        extensions.each { String key, String value ->
            if (element.ext.get(key) != value) {
                element.ext.put(key, value)
            }
        }
    }

    @Override
    void addToPendingRelationships(RelationshipProxy relationshipProxy) {
        if (!classification && relationshipProxy.relationshipTypeName == 'classification' && repository.equals(this, relationshipProxy.destination)) {
            classification = relationshipProxy.source.name
        }
        relationships << relationshipProxy
    }

    String toString() {
        "Proxy of $domain.simpleName[id: $id, classification: $classification, name: $name]"
    }

    @Override
    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other) {
        if (!(other instanceof DefaultCatalogueElementProxy) ) {
            throw new IllegalArgumentException("Can only merge with other default catalogue element proxies")
        }

        other.extensions.each { String key, String value ->
            if (value != null) {
                setExtension(key, value)
            }
        }

        other.parameters.each { String key, Object value ->
            if (value != null) {
                setParameter(key, value)
            }
        }

        other.relationships.each { RelationshipProxy relationship ->
            if (repository.equals(this, relationship.source)) {
                addToPendingRelationships(new RelationshipProxy(relationship.relationshipTypeName, this, relationship.destination, relationship.extensions))
            }

            if (repository.equals(this, relationship.destination)) {
                addToPendingRelationships(new RelationshipProxy(relationship.relationshipTypeName, relationship.source, this, relationship.extensions))
            }
        }

        other.replacedBy = this

        if (domain != other.domain) {
            if (domain == CatalogueElement) {
                domain = other.domain
            }
        }

        this
    }

    static normalizeWhitespace(Object o) {
        if (o instanceof CharSequence) {
            return o.toString().replaceAll(/(?m)\s+/, ' ').trim()
        }
        if (!o) {
            return ''
        }
        return o
    }
}
