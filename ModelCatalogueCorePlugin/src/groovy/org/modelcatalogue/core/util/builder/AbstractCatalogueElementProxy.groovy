package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

@Log4j
abstract class AbstractCatalogueElementProxy<T extends CatalogueElement> implements CatalogueElementProxy<T> {

    final Class<T> domain
    final String name

    protected final CatalogueElementProxyRepository repository

    private final Map<String, Object> parameters = [:]
    private final Map<String, String> extensions = [:]
    private final Set<RelationshipProxy> relationships = []

    private T resolved

    AbstractCatalogueElementProxy(CatalogueElementProxyRepository repository, Class<T> domain, String name) {
        this.repository = repository
        this.domain = domain
        this.name = name
    }

    @Override
    final T resolve() {
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

        parameters.put(key, value)
    }

    @Override
    void setExtension(String key, String value) {
        if (resolved) {
            throw new IllegalStateException("This catalogue element is already resolved!")
        }
        extensions.put(key, value)
    }

    abstract T findExisting()

    private T fill(T element) {
        if (!element) {
            return element
        }
        boolean changed = parameters.any { String key, Object value ->
            def realValue = value instanceof CatalogueElementProxy ? value.resolve() : value
            element.getProperty(key) != realValue
        }

        if (!changed) {
            if (element.extensions == extensions) {
                log.debug "$this has no changes"
                return element
            }
            // TODO: handle upgrade
            // TODO: do not clear metadata which are still the same
            element.ext.clear()
            element.ext.putAll(extensions)

            log.debug "$this has no changes in extensions"

            return element
        }

        // TODO: handle upgrade

        parameters.each { String key, Object value ->
            def realValue = value instanceof CatalogueElementProxy ? value.resolve() : value
            if (element.getProperty(key) != realValue) {
                element.setProperty(key, realValue)
            }
        }

        repository.save(element)

        element.ext.putAll(extensions)

        element
    }

    @Override
    void addToPendingRelationships(RelationshipProxy relationshipProxy) {
        relationships << relationshipProxy
    }

    @Override
    Set<Relationship> resolveRelationships() {
        relationships.collect {
            it.resolve()
        }
    }
}
