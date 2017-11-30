package org.modelcatalogue.core.diff

import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.util.HibernateHelper
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
    final DataClass parentClass
    final DataElement parentElement

    private Diff(String key, CatalogueElement element, Relationship relationship, Object selfValue, Object otherValue, DataClass parentClass, DataElement parentElement) {
        this.key = key
        this.element = element
        this.selfValue = selfValue
        this.otherValue = otherValue
        this.relationship = relationship
        this.parentClass = parentClass
        this.parentElement = parentElement
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

    static String keyForSelf(Long entityId) {
        return "entity:$entityId"
    }

    static Diff createEntityChange(CatalogueElement self, CatalogueElement other, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForSelf(self?.latestVersionId ?: self?.id), self, null, self, other, parentClass, parentElement)
    }

    static Diff createPropertyChange(String propertyKey, CatalogueElement source,  Object selfValue, Object otherValue, DataClass parentClass, DataElement parentElement) {
        return new Diff(propertyKey, source, null, selfValue, otherValue, parentClass, parentElement)
    }


    static Diff createExtensionChange(String extensionKey, CatalogueElement source ,String selfValue, String otherValue, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForExtension(extensionKey), source, null, selfValue, otherValue, parentClass, parentElement)
    }

    static Diff createMissingRelationship(CatalogueElement source, Relationship relationship, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForRelationship(relationship), source, relationship, relationship, null, parentClass, parentElement)
    }

    static Diff createRelationshipMetadataChange(Relationship relationship, String metadataKey, CatalogueElement source, String selfValue, String otherValue, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForRelationshipExtension(relationship, metadataKey), source, relationship, selfValue, otherValue, parentClass, parentElement)
    }

    static Diff createNewRelationship(CatalogueElement source, Relationship relationship, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForRelationship(relationship), source, relationship, null, relationship, parentClass, parentElement)
    }

    static Diff createEnumerationChange(CatalogueElement source, Long id, Enumeration selfEnumeration, Enumeration otherEnumeration, DataClass parentClass, DataElement parentElement) {
        return new Diff(keyForEnumeration(id), source, null, selfEnumeration, otherEnumeration, parentClass, parentElement)
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

    boolean isEntityChange() {
        return key.startsWith('entity:')
    }

    boolean isPropertyChange() {
        return !extensionChange && !relationshipChange && !enumerationChange && !relationshipExtensionChange && !entityChange
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

        //set description if removed "from", if added "to" i.e. removed x FROM y, or added x TO y
        String toFromIn = " to "

        if (isSelfMissing()) {
            builder << 'Removed '
            toFromIn = " from "
        } else if (isOtherMissing()) {
            builder << 'Added '
            toFromIn = " to "
        } else {
            builder  << 'Updated '
            toFromIn = " in "
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

        }


        if (isExtensionChange()) {
            builder << 'Metadata ' << key.substring(4) << toFromIn << element?.name

        }

        if (isRelationshipChange()) {
            switch (relationship.relationshipType.name) {
                case 'hierarchy':
                    builder << 'Data Class ' << relationship.destination.name << toFromIn << relationship.source.name

                    break;
                case 'containment':
                    builder << 'Data Element ' << relationship.destination.name << toFromIn << relationship.source.name

                    break;
                default:
                    builder << 'Relationship ' << GrailsNameUtils.getNaturalName(relationship.relationshipType.name)
            }
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
            builder << ' Metadata ' << relationshipExtensionKey << ' from ' << relationship.destination.name << ' to ' << relationship.source.name
        }

        if (isEntityChange()) {

            String type = "${(this?.element)? GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(this.element).simpleName):GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(this.otherValue).simpleName)}"

            builder << type

            if(this.parentClass && this.element && (type=="Data Class" || type=="Data Element")){
                builder << " ${this.element.name}${toFromIn}${this?.parentClass?.name}"
            }else if(this.element && !this.parentClass && type=="Data Class"){
                builder << " ${toFromIn}top level"
            }else if(this.element && this.parentElement){
                builder << " ${this.element.name}${toFromIn}${this.parentElement.name}"
            }

        }

        if (isPropertyChange()) {
            builder << "${GrailsNameUtils.getNaturalName(key)}"
        }
        return builder.toString()
    }
}
