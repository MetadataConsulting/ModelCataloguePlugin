package org.modelcatalogue.core.audit

import grails.web.JSONBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers

/**
 * Default auditor auditing the changes using the change table/entity.
 */
@Log4j
class DefaultAuditor implements Auditor {

    static List<String> IGNORED_PROPERTIES = ['password', 'version', 'versionNumber', 'outgoingRelationships', 'incomingRelationships', 'outgoingMappings', 'incomingMappings', 'latestVersionId', 'extensions']

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
        } else if (object instanceof Enum) {
            return object.toString()
        } else if (object instanceof CharSequence) {
            if (object.size() > 1950) {
                return object.toString()[0..1950] + '...'
            }
        }
        return object
    }

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
                property: "${extension.relationship.relationshipType.sourceToDestination} [${extension.name}]",
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.RELATIONSHIP_METADATA_CREATED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: "${extension.relationship.relationshipType.destinationToSource} [${extension.name}]",
                newValue: storeValue(extension.extensionValue),
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
                property: "${extension.relationship.relationshipType.sourceToDestination} [${extension.name}]",
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension.extensionValue),
                type: ChangeType.RELATIONSHIP_METADATA_UPDATED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: "${extension.relationship.relationshipType.destinationToSource} [${extension.name}]",
                oldValue: storeValue(extension.getPersistentValue('extensionValue')),
                newValue: storeValue(extension.extensionValue),
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
                property: "${extension.relationship.relationshipType.sourceToDestination} [${extension.name}]",
                oldValue: storeValue(extension.extensionValue),
                type: ChangeType.RELATIONSHIP_METADATA_DELETED
        )
        logChange(extension.relationship.destination,
                changedId: extension.relationship.destination.id,
                latestVersionId: extension.relationship.destination.latestVersionId ?: extension.relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: "${extension.relationship.relationshipType.destinationToSource} [${extension.name}]",
                oldValue: storeValue(extension.extensionValue),
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
        // TODO: changes in statuses might have to be handled differently
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
                newValue: storeValue(relationship.destination),
                oldValue: storeValue(relationship.classification),
                type: ChangeType.RELATIONSHIP_CREATED
        )
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.destinationToSource,
                newValue: storeValue(relationship.source),
                oldValue: storeValue(relationship.classification),
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
                newValue: storeValue(relationship.destination),
                oldValue: storeValue(relationship.classification),
                type: ChangeType.RELATIONSHIP_DELETED
        )
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: authorId ?: defaultAuthorId,
                property: relationship.relationshipType.destinationToSource,
                newValue: storeValue(relationship.source),
                oldValue: storeValue(relationship.classification),
                type: ChangeType.RELATIONSHIP_DELETED,
                otherSide: true
        )
    }
}
