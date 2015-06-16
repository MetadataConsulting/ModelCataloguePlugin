package org.modelcatalogue.core.audit

import grails.web.JSONBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipMarshallers

/**
 * Default auditor auditing the changes using the change table/entity.
 */
@Log4j
class DefaultAuditor implements Auditor {

    static List<String> IGNORED_PROPERTIES = ['password', 'version', 'versionNumber', 'outgoingRelationships', 'incomingRelationships', 'outgoingMappings', 'incomingMappings', 'latestVersionId', 'extensions']

    Long defaultAuthorId
    Long parentChangeId
    Boolean system


    Long logExternalChange(CatalogueElement source, String message, Long authorId) {
        logChange(source,
                changedId: source.id,
                latestVersionId: source.latestVersionId ?: source.id,
                authorId: authorId ?: defaultAuthorId,
                property: message,
                parentId: parentChangeId,
                type: ChangeType.EXTERNAL_UPDATE
        )
    }

    Long logNewVersionCreated(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                type: ChangeType.NEW_VERSION_CREATED
        )
    }

    Long logElementFinalized(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: 'status',
                type: ChangeType.ELEMENT_FINALIZED,
                oldValue: storeValue(element.status),
                newValue: storeValue(ElementStatus.FINALIZED)
        )
    }

    Long logElementDeprecated(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: 'status',
                type: ChangeType.ELEMENT_DEPRECATED,
                oldValue: storeValue(element.status),
                newValue: storeValue(ElementStatus.DEPRECATED)
        )
    }

    Long logElementCreated(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                type: ChangeType.NEW_ELEMENT_CREATED
        )
    }

    Long logNewMetadata(ExtensionValue extension, Long authorId) {
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.name,
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_CREATED
        )
    }

    Long logMetadataUpdated(ExtensionValue extension, Long authorId) {
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.name,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_UPDATED
        )
    }

    Long logMetadataDeleted(ExtensionValue extension, Long authorId) {
        if (!extension.element) {
            return null
        }
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.name,
                oldValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_DELETED
        )
    }

    Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return null
        }
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.destinationToSource,
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_CREATED,
                otherSide: true
        )
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.sourceToDestination,
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_CREATED
        )
    }

    Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return null
        }
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.destinationToSource,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_UPDATED,
                otherSide: true
        )
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_UPDATED
        )
    }

    Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return null
        }
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.destinationToSource,
                oldValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_DELETED,
                otherSide: true
        )
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: extension.relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_DELETED
        )
    }

    Long logElementDeleted(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                type: ChangeType.ELEMENT_DELETED,
                oldValue: storeValue(element)
        )
    }

    Long logElementUpdated(CatalogueElement element, Long authorId) {
        for (String name in element.dirtyPropertyNames) {
            if (name in IGNORED_PROPERTIES) {
                continue
            }

            def originalValueRaw = element.getPersistentValue(name)
            def newValueRaw = element.getProperty(name)

            if (originalValueRaw instanceof String && newValueRaw instanceof String) {
                if (originalValueRaw.trim() == newValueRaw.trim()) {
                    continue
                }
            }

            def originalValue = storeValue(originalValueRaw)
            def newValue = storeValue(newValueRaw)

            ChangeType type = ChangeType.PROPERTY_CHANGED

            logChange(element,
                    changedId: element.id,
                    latestVersionId: element.latestVersionId ?: element.id,
                    authorId: authorId ?: defaultAuthorId,
                    parentId: parentChangeId,
                    type: type,
                    property: name,
                    oldValue: originalValue,
                    newValue: newValue,
                    system: name == 'status'
            )
        }
    }

    @Override
    Long logMappingCreated(Mapping mapping, Long authorId) {
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_CREATED,
                otherSide: true
        )
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_CREATED
        )
    }

    @Override
    Long logMappingDeleted(Mapping mapping, Long authorId) {
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                oldValue: storeValue(mapping),
                type: ChangeType.MAPPING_DELETED,
                otherSide: true
        )
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                oldValue: storeValue(mapping),
                type: ChangeType.MAPPING_DELETED
        )
    }

    @Override
    Long logMappingUpdated(Mapping mapping, Long authorId) {
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                oldValue: storeValue(mapping.getPersistentValue('mapping')),
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_UPDATED,
                otherSide: true
        )
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                oldValue: storeValue(mapping.getPersistentValue('mapping')),
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_UPDATED
        )
    }

    Long logChange(Map <String, Object> changeProps, CatalogueElement element) {
        try {
            Change change = new Change(changeProps)
            change.system = change.system || system
            change.validate()
            if (change.hasErrors()) {
                log.warn FriendlyErrors.printErrors("Error logging ${changeProps.type} of $element", change.errors)
            }
            change.save()
            return change.id
        } catch (Exception e) {
            log.error "Exception writing audit log for $element", e
        }

    }

    Long logNewRelation(Relationship relationship, Long authorId) {
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.destinationToSource,
                newValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_CREATED,
                otherSide: true,
                system: relationship.relationshipType.system || relationship.relationshipType == RelationshipType.classificationType || system
        )
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.sourceToDestination,
                newValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_CREATED,
                system: relationship.relationshipType.system || relationship.relationshipType == RelationshipType.classificationType || system
        )
    }

    Long logRelationRemoved(Relationship relationship, Long authorId) {
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.destinationToSource,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_DELETED,
                otherSide: true,
                system: relationship.relationshipType.system || relationship.relationshipType == RelationshipType.classificationType || system
        )
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_DELETED,
                system: relationship.relationshipType.system || relationship.relationshipType == RelationshipType.classificationType || system
        )
    }


    Long logRelationArchived(Relationship relationship, Long authorId) {
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.destinationToSource,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_ARCHIVED,
                otherSide: true,
                system: true
        )
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                parentId: parentChangeId,
                property: relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_ARCHIVED,
                system: true
        )
    }

    static String storeValue(Object object) {
        Object toStore = objectToStore(object)
        if (toStore == null) {
            return null
        }
        new JSONBuilder().build {
            toStore instanceof Map ? toStore : [value: toStore]
        }.toString()
    }

    private static objectToStore(Object object) {
        if (object instanceof CatalogueElement) {
            return CatalogueElementMarshaller.minimalCatalogueElementJSON(object)
        }
        if (object instanceof Mapping) {
            return [
                    source : CatalogueElementMarshaller.minimalCatalogueElementJSON(object.source),
                    destination: CatalogueElementMarshaller.minimalCatalogueElementJSON(object.destination),
                    mapping: object.mapping,
                    id     : object.id
            ]
        }
        if (object instanceof RelationshipMetadata) {
            return [
                    name: object.name,
                    extensionValue: object.extensionValue,
                    relationship: objectToStore(object.relationship)
            ]
        }
        if (object instanceof Relationship){
            return RelationshipMarshallers.getRelationshipAsMap(object)
        }
        if (object instanceof Enum) {
            return object.toString()
        }
        if (object instanceof CharSequence) {
            if (object.size() > 14950) {
                return object.toString()[0..14950] + '...(text truncated)'
            }
        }
        return object
    }

    static Object readValue(String string) {
        if (string == null) {
            return null
        }
        JsonSlurper jsonSlurper = new JsonSlurper()
        JSONObject object = jsonSlurper.parseText(string) as JSONObject
        if (object.containsKey('value')) {
            return object.value
        }
        if (object.containsKey('mapping') || object.containsKey('extensionValue')) {
            return object
        }
        if (object.containsKey('elementType')) {
            if (object.elementType == Relationship.name) {
                return object
            }
            return Class.forName(object.elementType).get(object.id)
        }
        throw new IllegalArgumentException("Unsupported stored value: $string")
    }
}
