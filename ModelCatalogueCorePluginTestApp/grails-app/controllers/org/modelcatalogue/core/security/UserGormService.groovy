package org.modelcatalogue.core.security

import grails.transaction.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class UserGormService {

    @Transactional(readOnly = true)
    User findByUsername(String usernameParam) {
        User.where { username == usernameParam }.get()
    }

    @Transactional(readOnly = true)
    User findById(long id) {
        User.get(id)
    }
}