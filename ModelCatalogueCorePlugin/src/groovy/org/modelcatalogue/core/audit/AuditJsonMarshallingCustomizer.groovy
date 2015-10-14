package org.modelcatalogue.core.audit

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.marshalling.JsonMarshallingCustomizer
import org.springframework.beans.factory.annotation.Autowired

class AuditJsonMarshallingCustomizer extends JsonMarshallingCustomizer {

    @Autowired AuditService auditService


    @Override def customize(Object el, Object json) {
        def result = json ?: [:]
        if (el instanceof CatalogueElement) {
            result.history = [count: el.countVersions(), itemType: el.getClass().name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history"]
            result.changes = [count: auditService.getChanges([:], el).total, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/changes"]
        }
        if (el instanceof DataModel) {
            result.activity = [count: auditService.getGlobalChanges([:], DataModelFilter.includes(el)).total, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/activity"]
        } else if (el instanceof User) {
            result.activity = [count: auditService.getChangesForUser([:], el).total, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/activity"]
        }
        return result
    }
}
