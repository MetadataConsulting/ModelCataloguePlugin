package org.modelcatalogue.core.security

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.publishing.PublishingContext

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

    static relationships = [
        outgoing: [favourite: 'favourites'],
        incoming: [classificationFilter: 'filteredBy']
    ]

    static mapping = {
        password column: '`password`'
    }

    static hasMany = [oAuthIDs: OAuthID]

    void setUsername(String username) {
        if (!getName()) {
            setName(username)
        }
        this.username = username
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    void beforeInsert() {
        super.beforeInsert()
        encodePassword()
        if (!getName()) {
            setName(getUsername())
        }
    }

    void beforeUpdate() {
        if (isDirty('password') && !hasErrors()) {
            encodePassword()
        }
        super.beforeUpdate()
    }

    protected void encodePassword() {
        password = modelCatalogueSecurityService.encodePassword(password)
    }

    @Override
    void beforeDraftPersisted(PublishingContext context) {
        super.beforeDraftPersisted(context)
        String randomUsername = username
        while (User.countByUsername(randomUsername)) {
            randomUsername = "${username[0..(username.indexOf('(') - 1)]} (${UUID.randomUUID().toString()})"
        }
        username = randomUsername
        // made the user account unable to sign in
        enabled = false
        accountExpired = true
        accountLocked = true
        passwordExpired = true
    }
}
