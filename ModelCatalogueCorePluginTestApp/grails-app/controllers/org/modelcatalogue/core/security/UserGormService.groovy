package org.modelcatalogue.core.security

import grails.transaction.Transactional

class UserGormService {

    @Transactional(readOnly = true)
    User findByUsername(String usernameParam) {
        User.where { username == usernameParam }.get()
    }

    @Transactional(readOnly = true)
    User findById(Long userId) {
        User.where { id == userId }.get()
    }
}