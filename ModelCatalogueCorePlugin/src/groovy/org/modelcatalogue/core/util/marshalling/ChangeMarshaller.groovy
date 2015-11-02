package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.springframework.beans.factory.annotation.Autowired

@Log4j
class ChangeMarshaller extends AbstractMarshaller {

    @Autowired AuditService auditService

    ChangeMarshaller() {
        super(Change)
    }

    protected Map<String, Object> prepareJsonMap(change) {
        if (!change) return [:]
        Map<String, Object> ret = [
                elementType:    Change.name,
                id:             change.id,
                type:           change.type.toString(),
                undoSupported:  change.type.undoSupported,
                otherSide:      change.otherSide,
                system:         change.system,
                dateCreated:    change.dateCreated,
                property:       change.property,
                oldValue:       change.oldValue != null ? JSON.parse(change.oldValue as String) : null,
                newValue:       change.newValue != null ? JSON.parse(change.newValue as String) : null,
                undone:         change.undone,
                parent:         Change.get(change.parentId),
                link:           "/change/$change.id",
                changes:        [count: auditService.getSubChanges([:], change).total, itemType: Change.name, link: "/${GrailsNameUtils.getPropertyName(change.getClass())}/$change.id/changes"]
        ]
        try {
            ret.putAll(
                changed:        getPotentiallyDeletedInfo(change.changedId),
                latestVersion:  getPotentiallyDeletedInfo(change.latestVersionId),
                author:         getPotentiallyDeletedInfo(change.authorId)
            )
            return ret
        } catch (Exception ex) {
            log.error("Exception rendering change $change", ex)
            return ret
        }

    }

    private static Object getPotentiallyDeletedInfo(Long id) {
        CatalogueElement existing = CatalogueElement.get(id)
        if (existing) {
            return CatalogueElementMarshaller.minimalCatalogueElementJSON(existing)
        }

        Change change = Change.findByChangedIdAndType(id, ChangeType.ELEMENT_DELETED)

        if (change) {
            def deleted = JSON.parse(change.oldValue as String)
            deleted.deleted = true
            return deleted
        }

        return null
    }
}
