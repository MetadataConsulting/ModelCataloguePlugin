package org.modelcatalogue.core.audit

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.marshalling.JsonMarshallingCustomizer
import org.springframework.beans.factory.annotation.Autowired

class AuditJsonMarshallingCustomizer extends JsonMarshallingCustomizer {

    @Autowired AuditService auditService


    @Override def customize(Object el, Object json) {
        def result = json ?: [:]
        if (el instanceof CatalogueElement) {
            result.changes = [count: Integer.MAX_VALUE /* auditService.getChanges([:], el).total */, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/changes"]
        }
        if (el instanceof DataModel) {
            result.activity = [count: Integer.MAX_VALUE, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/activity"]
        } else if (el instanceof User) {
            result.activity = [count: Integer.MAX_VALUE, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/activity"]
        }
        return result
    }
}
