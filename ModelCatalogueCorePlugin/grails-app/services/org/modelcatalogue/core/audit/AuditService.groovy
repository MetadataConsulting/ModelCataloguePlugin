package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.FriendlyErrors

class AuditService {

    static transactional = false

    static List<String> IGNORED_PROPERTIES = ['password', 'version', 'outgoingRelationships', 'incomingRelationships', 'outgoingMappings', 'incomingMappings']

    def modelCatalogueSecurityService

    void logElementCreated(CatalogueElement element) {
        logChange(element,
            changedId: element.id,
            latestVersionId: element.latestVersionId ?: element.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            type: element.latestVersionId && element.latestVersionId != element.id ? ChangeType.NEW_VERSION_CREATED : ChangeType.NEW_ELEMENT_CREATED
        )
    }

    void logNewMetadata(ExtensionValue extension) {
        logChange(extension.element,
            changedId: extension.element.id,
            latestVersionId: extension.element.latestVersionId ?: extension.element.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            property: extension.name,
            newValue: extension.extensionValue,
            type: ChangeType.METADATA_CREATED
        )
    }

    void logMetadataUpdated(ExtensionValue extension) {
        logChange(extension.element,
            changedId: extension.element.id,
            latestVersionId: extension.element.latestVersionId ?: extension.element.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            property: extension.name,
            oldValue: extension.getPersistentValue('extensionValue'),
            newValue: extension.extensionValue,
            type: ChangeType.METADATA_UPDATED
        )
    }
    void logMetadataDeleted(ExtensionValue extension) {
        if (!extension.element) {
            return
        }
        logChange(extension.element,
            changedId: extension.element.id,
            latestVersionId: extension.element.latestVersionId ?: extension.element.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            property: extension.name,
            oldValue: extension.extensionValue,
            type: ChangeType.METADATA_DELETED
        )
    }

    void logElementDeleted(CatalogueElement element) {
        logChange(element,
            changedId: element.id,
            latestVersionId: element.latestVersionId ?: element.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            type: ChangeType.ELEMENT_DELETED
        )
    }

    void logElementUpdated(CatalogueElement element) {
        // TODO: changes in statuses might have to be handled differently
        for (String name in element.dirtyPropertyNames) {
            if (name in IGNORED_PROPERTIES) {
                continue
            }
            def originalValue = element.getPersistentValue(name)
            def newValue = element.getProperty(name)
            logChange(element,
                changedId: element.id,
                latestVersionId: element.latestVersionId ?: element.id,
                authorId: modelCatalogueSecurityService.currentUser?.id,
                type: ChangeType.PROPERTY_CHANGED,
                property: name,
                oldValue: storeValue(originalValue),
                newValue: storeValue(newValue)
            )
        }
    }


    void logChange(Map <String, Object> changeProps, CatalogueElement element) {
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

    void logNewRelation(Relationship relationship) {
        logChange(relationship.source,
            changedId: relationship.source.id,
            latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            property: relationship.relationshipType.sourceToDestination,
            newValue: storeValue(relationship.destination),
            oldValue: storeValue(relationship.classification),
            type: ChangeType.RELATIONSHIP_CREATED
        )
        logChange(relationship.destination,
            changedId: relationship.destination.id,
            latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
            authorId: modelCatalogueSecurityService.currentUser?.id,
            property: relationship.relationshipType.destinationToSource,
            newValue: storeValue(relationship.source),
            oldValue: storeValue(relationship.classification),
            type: ChangeType.RELATIONSHIP_CREATED
        )
    }

    void logRelationRemoved(Relationship relationship) {
        logChange(relationship.source,
                changedId: relationship.source.id,
                latestVersionId: relationship.source.latestVersionId ?: relationship.source.id,
                authorId: modelCatalogueSecurityService.currentUser?.id,
                property: relationship.relationshipType.sourceToDestination,
                newValue: storeValue(relationship.destination),
                oldValue: storeValue(relationship.classification),
                type: ChangeType.RELATIONSHIP_DELETED
        )
        logChange(relationship.destination,
                changedId: relationship.destination.id,
                latestVersionId: relationship.destination.latestVersionId ?: relationship.destination.id,
                authorId: modelCatalogueSecurityService.currentUser?.id,
                property: relationship.relationshipType.destinationToSource,
                newValue: storeValue(relationship.source),
                oldValue: storeValue(relationship.classification),
                type: ChangeType.RELATIONSHIP_DELETED
        )
    }


    static String storeValue(Object object) {
        if (object instanceof CatalogueElement) {
            return object.modelCatalogueId
        }

        String ret = object?.toString()

        if (ret && ret.size() > 2000) {
            return ret[0..2000]
        }
        return ret
    }
}
