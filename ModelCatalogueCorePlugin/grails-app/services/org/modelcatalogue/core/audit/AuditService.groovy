package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
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
                oldValue: originalValue?.toString(),
                newValue: newValue?.toString()
            )
        }
    }


    void logChange(Map <String, Object> changeProps, CatalogueElement element) {
        try {
            if (element.hasErrors() || !element.id) {
                log.warn "Error logging ${changeProps.type} of $element, not ready for queries"
                return
            }
            Change change = new Change(changeProps).save()
            if (change.hasErrors()) {
                log.warn FriendlyErrors.printErrors("Error logging ${changeProps.type} of $element", change.errors)
            }
        } catch (Exception e) {
            log.error "Exception writing audit log for $element", e
        }

    }

}
