package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.security.User

class UserMarshaller extends CatalogueElementMarshallers {

    UserMarshaller() {
        super(User)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)

        ret.putAll username: el.username,
                email: el.email,
                enabled: el.enabled,
                accountExpired: el.accountExpired,
                accountLocked: el.accountLocked,
                passwordExpired: el.passwordExpired,
                activity: [count: auditService.getChangesForUser([:], el).total, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/activity"]

        // rename the filteredBy to classifications
        ret.classifications = ret.remove('filteredBy')

        ret
    }

}




