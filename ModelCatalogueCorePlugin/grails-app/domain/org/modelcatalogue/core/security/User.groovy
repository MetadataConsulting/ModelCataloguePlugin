package org.modelcatalogue.core.security

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification

class User extends CatalogueElement {

    transient modelCatalogueSecurityService

    String username
    String password
    String email
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    Classification defaultClassification

    static constraints = {
        username blank: false, unique: true, maxSize: 255
        password blank: false, maxSize: 255
        email    email: true, maxSize: 255
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = modelCatalogueSecurityService.encodePassword(password)
    }
}
