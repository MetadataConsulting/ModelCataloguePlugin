package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.security.User

class UserMarshaller extends CatalogueElementMarshaller {

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
                role: getStrongestRole(el as User),
                status: el.enabled ? 'FINALIZED' : 'DEPRECATED' // just to color rows, does not make any sense otherwise

        // rename the filteredBy to classifications
        ret.dataModels = ret.remove('filteredBy')

        ret
    }

    static String getStrongestRole(User user) {
        Set<String> roles = user.authorities*.authority
        !roles.isEmpty() ? roles.first() : null
    }
}




