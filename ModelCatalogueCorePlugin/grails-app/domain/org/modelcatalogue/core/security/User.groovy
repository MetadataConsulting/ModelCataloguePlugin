package org.modelcatalogue.core.security

import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ExtendibleElement

class User extends ExtendibleElement {

    transient modelCatalogueSecurityService

    String username
    String password
    String email
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    /**
     * Set of classifications choosen by the user tobe shown
     */
    Set<Classification> classifications

    static constraints = {
        username blank: false, unique: true, maxSize: 255
        password blank: false, maxSize: 255
        email    nullable: true, email: true, maxSize: 255
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
        if (isDirty('password') && !hasErrors()) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = modelCatalogueSecurityService.encodePassword(password)
    }
}
