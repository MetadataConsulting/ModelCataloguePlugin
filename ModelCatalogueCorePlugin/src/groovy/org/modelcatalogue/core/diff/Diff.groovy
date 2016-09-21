package org.modelcatalogue.core.diff

import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log4j
class Diff {

    private final Pattern REL_EXT_PATTERN = Pattern.compile(/rex:(\d+)=\[(.+?)\/(.+)]=>(\d+)/)

    final String key
    final CatalogueElement element
    final Relationship relationship
    final Object selfValue
    final Object otherValue

    private Diff(String key, CatalogueElement element, Relationship relationship, Object selfValue, Object otherValue) {
        this.key = key
        this.element = element
        this.selfValue = selfValue
        this.otherValue = otherValue
        this.relationship = relationship
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
        return "rex:${relationship.source.latestVersionId ?: relationship.source.id}=[${relationship.relationshipType.name}/$extensionName]=>${relationship.destination.latestVersionId ?: relationship.destination.id}"
    }

    static String keyForEnumeration(Long id) {
        if (!id) {
            return null
        }
        return "enum:$id"
    }

    static Diff createPropertyChange(String propertyKey, CatalogueElement source,  Object selfValue, Object otherValue) {
        return new Diff(propertyKey, source, null, selfValue, otherValue)
    }


    static Diff createExtensionChange(String extensionKey, CatalogueElement source ,String selfValue, String otherValue) {
        return new Diff(keyForExtension(extensionKey), source, null, selfValue, otherValue)
    }

    static Diff createMissingRelationship(CatalogueElement source, Relationship relationship) {
        return new Diff(keyForRelationship(relationship), source, relationship, relationship, null)
    }

    static Diff createRelationshipMetadataChange(Relationship relationship, String metadataKey, CatalogueElement source, String selfValue, String otherValue) {
        return new Diff(keyForRelationshipExtension(relationship, metadataKey), source, relationship, selfValue, otherValue)
    }

    static Diff createNewRelationship(CatalogueElement source, Relationship relationship) {
        return new Diff(keyForRelationship(relationship), source, relationship, null, relationship)
    }

    static Diff createEnumerationChange(CatalogueElement source, Long id, Enumeration selfEnumeration, Enumeration otherEnumeration) {
        return new Diff(keyForEnumeration(id), source, null, selfEnumeration, otherEnumeration)
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

    boolean isRelationshipExtensionChange() {
        return key.startsWith('rex:')
    }

    boolean isEnumerationChange() {
        return key.startsWith('enum:')
    }

    boolean isPropertyChange() {
        return !extensionChange && !relationshipChange && !enumerationChange && !relationshipExtensionChange
    }

    private String getRelationshipExtensionKey() {
        if (isRelationshipExtensionChange()) {
            Matcher matcher = REL_EXT_PATTERN.matcher(key)
            if (matcher.matches()) {
                return matcher.group(3)

            }
        }
        return null
    }

    String getChangeDescription() {
        StringBuilder builder = new StringBuilder()

        if (isSelfMissing()) {
            builder << 'Removed '
        } else if (isOtherMissing()) {
            builder << 'Added '
        } else {
            builder  << 'Updated '
        }

        if (isEnumerationChange()) {
            Enumeration selfEnumeration = selfValue as Enumeration
            Enumeration otherEnumeration = otherValue as Enumeration

            if (selfEnumeration && otherEnumeration) {
                if (selfEnumeration.deprecated && !otherEnumeration.deprecated) {
                    builder = new StringBuilder("Deprecated ")
                } else if (!selfEnumeration.deprecated && otherEnumeration.deprecated) {
                    builder = new StringBuilder("Removed deprecation of ")
                }
            }

            builder << 'Enumeration ' << (selfEnumeration?.key ?: otherEnumeration?.key)

            return builder.toString()
        }


        if (isExtensionChange()) {
            builder << 'Metadata ' << key.substring(4)
            return builder.toString()
        }

        if (isRelationshipChange()) {
            switch (relationship.relationshipType.name) {
                case 'hierarchy':
                    builder << 'Data Class'
                    break;
                case 'containment':
                    builder << 'Data Element'
                    break;
                default:
                    builder << 'Relationship ' << GrailsNameUtils.getNaturalName(relationship.relationshipType.name)
            }
            return builder.toString()
        }

        if (isRelationshipExtensionChange()) {
            switch (relationship.relationshipType.name) {
                case 'hierarchy':
                    builder << 'Hierarchy'
                    break;
                case 'containment':
                    builder << 'Containment'
                    break;
                default:
                    builder << 'Relationship ' << GrailsNameUtils.getNaturalName(relationship.relationshipType.name)
            }
            builder << ' Metadata ' << relationshipExtensionKey
            return builder.toString()
        }

        if (isPropertyChange()) {
            builder << GrailsNameUtils.getNaturalName(key)
            return builder.toString()
        }

        // should never happen
        return key
    }
}
