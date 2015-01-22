package org.modelcatalogue.core.util.builder

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

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

    Relationship resolve() {
        try {
            RelationshipType type = RelationshipType.readByName(relationshipTypeName)
            T sourceElement = source.resolve()
            U destinationElement = destination.resolve()
            if (!sourceElement.readyForQueries) {
                throw new IllegalStateException("Source element $sourceElement is not ready to be part of the relationship ${toString()}")
            }
            if (!destinationElement.readyForQueries) {
                throw new IllegalStateException("Destination element $destinationElement is not ready to be part of the relationship ${toString()}")
            }
            Relationship relationship = sourceElement.createLinkTo(destinationElement, type)
            if (relationship.hasErrors()) {
                log.error(relationship.errors)
                throw new IllegalStateException("Cannot create relationship of type $relationshipTypeName between $sourceElement and $destinationElement.")
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
