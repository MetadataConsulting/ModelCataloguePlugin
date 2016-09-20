package org.modelcatalogue.core.diff

import groovy.util.logging.Log4j
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration

@Log4j
class Diff {
    final String key
    final Object selfValue
    final Object otherValue

    private Diff(String key, Object selfValue, Object otherValue) {
        this.key = key
        this.selfValue = selfValue
        this.otherValue = otherValue
    }

    static String keyForProperty(String propertyName) {
        return propertyName
    }

    static String keyForExtension(String extensionName) {
        if (!extensionName) {
            return null
        }
        return "ext:$extensionName"
    }

    static String keyForRelationship(Relationship relationship) {
        if (!relationship) {
            return null
        }
        return "rel:${relationship.source.latestVersionId ?: relationship.source.id}=[${relationship.relationshipType.name}]=>${relationship.destination.latestVersionId ?: relationship.destination.id}"
    }

    static String keyForRelationshipExtension(Relationship relationship, String extensionName) {
        if (!relationship) {
            return null
        }
        return "rel:${relationship.source.latestVersionId ?: relationship.source.id}=[${relationship.relationshipType.name}/$extensionName]=>${relationship.destination.latestVersionId ?: relationship.destination.id}"
    }

    static String keyForEnumeration(Long id) {
        if (!id) {
            return null
        }
        return "enum:$id"
    }

    static Diff createPropertyChange(String propertyKey, Object selfValue, Object otherValue) {
        return new Diff(propertyKey, selfValue, otherValue)
    }


    static Diff createExtensionChange(String extensionKey, String selfValue, String otherValue) {
        return new Diff(keyForExtension(extensionKey), selfValue, otherValue)
    }

    static Diff createMissingRelationship(Relationship relationship) {
        return new Diff(keyForRelationship(relationship), relationship, null)
    }

    static Diff createRelationshipMetadataChange(Relationship relationship, String metadataKey, String selfValue, String otherValue) {
        return new Diff(keyForRelationshipExtension(relationship, metadataKey), selfValue, otherValue)
    }

    static Diff createNewRelationship(Relationship relationship) {
        return new Diff(keyForRelationship(relationship), null, relationship)
    }

    static Diff createEnumerationChange(Long id, Enumeration selfEnumeration, Enumeration otherEnumeration) {
        return new Diff(keyForEnumeration(id), selfEnumeration, otherEnumeration)
    }

    @Override
    String toString() {
        return "${key}: $selfValue => $otherValue"
    }

    boolean isOtherMissing() {
        return otherValue == null
    }

    boolean isSelfMissing() {
        return selfValue == null
    }

    boolean isUpdate() {
        return !otherMissing && !selfMissing
    }

    boolean isExtensionChange() {
        return key.startsWith('ext:')
    }

    boolean isRelationshipChange() {
        return key.startsWith('rel:')
    }

    boolean isEnumerationChange() {
        return key.startsWith('enum:')
    }

    boolean isPropertyChange() {
        return !extensionChange && !relationshipChange && !enumerationChange
    }
}
