package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.security.User

class UserMarshaller extends ExtendibleElementMarshallers {

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
                classifications: el.classifications.collect { minimalCatalogueElementJSON(it) }
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            username el.username
            email el.email
            enabled el.enabled
            accountExpired el.accountExpired
            accountLocked el.accountLocked
            passwordExpired el.passwordExpired
        }
    }
}




