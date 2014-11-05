package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.security.User

class UserMarshaller extends PublishedElementMarshallers {

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
                passwordExpired: el.passwordExpired
        ret
    }

}




