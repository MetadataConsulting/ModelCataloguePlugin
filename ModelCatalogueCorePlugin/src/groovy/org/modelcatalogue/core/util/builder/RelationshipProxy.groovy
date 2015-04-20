package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.FriendlyErrors

@Log4j
class RelationshipProxy<T extends CatalogueElement, U extends CatalogueElement> implements ExtensionAwareBuilder {

    final String relationshipTypeName
    final CatalogueElementProxy<T> source
    final CatalogueElementProxy<U> destination
    final Map<String, String> extensions = [:]

    RelationshipProxy(String relationshipTypeName, CatalogueElementProxy<T> source, CatalogueElementProxy<T> destination, @DelegatesTo(ExtensionAwareBuilder) Closure extensions) {
        this.relationshipTypeName = relationshipTypeName
        this.source = source
        this.destination = destination
        this.with extensions
    }

    RelationshipProxy(String relationshipTypeName, CatalogueElementProxy<T> source, CatalogueElementProxy<T> destination, Map<String, String> extensions) {
        this.relationshipTypeName = relationshipTypeName
        this.source = source
        this.destination = destination
        if (extensions) {
            this.extensions.putAll(extensions)
        }
    }

    Relationship resolve(CatalogueElementProxyRepository repository) {
        try {
            Relationship relationship = repository.resolveRelationship(this)
            if (relationship.hasErrors()) {
                log.error(FriendlyErrors.printErrors("Cannot create relationship of type  $relationshipTypeName between $source and $destination", relationship.errors))
                throw new IllegalStateException("Cannot create relationship of type $relationshipTypeName between $source and $destination.")
            }
            if (extensions) {
                relationship.ext.putAll(extensions)
            }
            return relationship
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve $this:\n\n$e", e)
        }

    }

    void ext(String key, String value) {
        extensions[key] = value
    }

    void ext(Map<String, String> values) {
        extensions.putAll(values)
    }


    @Override
    String toString() {
        "Proxy for Relationship[type: $relationshipTypeName, source: $source, destination: $destination]"
    }
}
