package org.modelcatalogue.core.security

import org.modelcatalogue.core.CatalogueElement

class User extends CatalogueElement {

    transient modelCatalogueSecurityService

    String username
    String password
    String email
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

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

    @Override
    protected void beforeArchive() {
        super.beforeArchive()
        String randomUsername = username
        while (User.countByUsername(randomUsername)) {
            randomUsername = "$username (${UUID.randomUUID().toString()})"
        }
        username = randomUsername
        // made the user account unable to sign in
        enabled = false
        accountExpired = true
        accountLocked = true
        passwordExpired = true
    }
}
