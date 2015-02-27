package org.modelcatalogue.core.audit

import grails.web.JSONBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers
import org.modelcatalogue.core.util.marshalling.RelationshipMarshallers

/**
 * Default auditor auditing the changes using the change table/entity.
 */
@Log4j
class DefaultAuditor implements Auditor {

    static List<String> IGNORED_PROPERTIES = ['password', 'version', 'versionNumber', 'outgoingRelationships', 'incomingRelationships', 'outgoingMappings', 'incomingMappings', 'latestVersionId', 'extensions']

    Long defaultAuthorId

    void logElementCreated(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                type: element.latestVersionId && element.latestVersionId != element.id ? ChangeType.NEW_VERSION_CREATED : ChangeType.NEW_ELEMENT_CREATED
        )
    }

    void logNewMetadata(ExtensionValue extension, Long authorId) {
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.name,
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_CREATED
        )
    }

    void logMetadataUpdated(ExtensionValue extension, Long authorId) {
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.name,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_UPDATED
        )
    }

    void logMetadataDeleted(ExtensionValue extension, Long authorId) {
        if (!extension.element) {
            return
        }
        logChange(extension.element,
                changedId: extension.element.id,
                latestVersionId: extension.element.latestVersionId ?: extension.element.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.name,
                oldValue: storeValue(extension.extensionValue),
                type: ChangeType.METADATA_DELETED
        )
    }

    void logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return
        }
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.sourceToDestination,
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_CREATED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.destinationToSource,
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_CREATED,
                otherSide: true
        )
    }

    void logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return
        }
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_UPDATED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.destinationToSource,
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_UPDATED,
                otherSide: true
        )
    }

    void logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        if (extension.relationship.relationshipType.system) {
            return
        }
        logChange(extension.relationship.source,
                changedId: extension.relationship.source.id,
                latestVersionId: extension.relationship.source.latestVersionId ?: extension.relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_DELETED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: extension.relationship.relationshipType.destinationToSource,
                oldValue: storeValue(extension),
                type: ChangeType.RELATIONSHIP_METADATA_DELETED,
                otherSide: true
        )
    }

    void logElementDeleted(CatalogueElement element, Long authorId) {
        logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: authorId ?: defaultAuthorId,
                type: ChangeType.ELEMENT_DELETED,
                oldValue: storeValue(element)
        )
    }

    void logElementUpdated(CatalogueElement element, Long authorId) {
        for (String name in element.dirtyPropertyNames) {
            if (name in IGNORED_PROPERTIES) {
                continue
            }

            def originalValue = storeValue(element.getPersistentValue(name))
            def newValueRaw = element.getProperty(name)
            def newValue = storeValue(newValueRaw)

            ChangeType type = ChangeType.PROPERTY_CHANGED

            if (name == 'status') {
                if (newValueRaw == ElementStatus.FINALIZED) {
                    type = ChangeType.ELEMENT_FINALIZED
                } else if (newValueRaw == ElementStatus.DEPRECATED) {
                    type = ChangeType.ELEMENT_DEPRECATED
                } else {
                    continue
                }
            }

            logChange(element,
                    changedId: element.id,
                    latestVersionId: element.latestVersionId ?: element.id,
                    authorId: authorId ?: defaultAuthorId,
                    type: type,
                    property: name,
                    oldValue: originalValue,
                    newValue: newValue
            )
        }
    }

    @Override
    void logMappingCreated(Mapping mapping, Long authorId) {
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_CREATED
        )
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_CREATED,
                otherSide: true
        )
    }

    @Override
    void logMappingDeleted(Mapping mapping, Long authorId) {
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                oldValue: storeValue(mapping),
                type: ChangeType.MAPPING_DELETED
        )
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                oldValue: storeValue(mapping),
                type: ChangeType.MAPPING_DELETED,
                otherSide: true
        )
    }

    @Override
    void logMappingUpdated(Mapping mapping, Long authorId) {
        logChange(mapping.source,
                changedId: mapping.source.id,
                latestVersionId: mapping.source.latestVersionId ?: mapping.source.id,
                authorId: authorId ?: defaultAuthorId,
                oldValue: storeValue(mapping.getPersistentValue('mapping')),
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_UPDATED
        )
        logChange(mapping.destination,
                changedId: mapping.destination.id,
                latestVersionId: mapping.destination.latestVersionId ?: mapping.destination.id,
                authorId: authorId ?: defaultAuthorId,
                oldValue: storeValue(mapping.getPersistentValue('mapping')),
                newValue: storeValue(mapping),
                type: ChangeType.MAPPING_UPDATED,
                otherSide: true
        )    
    }

    static void logChange(Map <String, Object> changeProps, CatalogueElement element) {
        try {
            Change.withNewSession {
                if (element.hasErrors() || !element.id) {
                    log.warn "Error logging ${changeProps.type} of $element, not ready for queries"
                    return
                }
                Change change = new Change(changeProps)
                change.validate()
                if (change.hasErrors()) {
                    log.warn FriendlyErrors.printErrors("Error logging ${changeProps.type} of $element", change.errors)
                }
                change.save()
            }
        } catch (Exception e) {
            log.error "Exception writing audit log for $element", e
        }

    }

    void logNewRelation(Relationship relationship, Long authorId) {
        if (relationship.relationshipType.system) {
            return
        }
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.sourceToDestination,
                newValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_CREATED
        )
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.destinationToSource,
                newValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_CREATED,
                otherSide: true
        )
    }

    void logRelationRemoved(Relationship relationship, Long authorId) {
        if (relationship.relationshipType.system) {
            return
        }
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.sourceToDestination,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_DELETED
        )
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.destinationToSource,
                oldValue: storeValue(relationship),
                type: ChangeType.RELATIONSHIP_DELETED,
                otherSide: true
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
            return CatalogueElementMarshallers.minimalCatalogueElementJSON(object)
        }
        if (object instanceof Mapping) {
            return [
                    source : CatalogueElementMarshallers.minimalCatalogueElementJSON(object.source),
                    destination: CatalogueElementMarshallers.minimalCatalogueElementJSON(object.destination),
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
            def ret = RelationshipMarshallers.getRelationshipAsMap(object)
            ret.remove('ext')
            return ret
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
