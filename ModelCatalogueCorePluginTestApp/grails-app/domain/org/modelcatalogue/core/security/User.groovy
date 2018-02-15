package org.modelcatalogue.core.security

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.MaxActiveUsersService
import org.modelcatalogue.core.publishing.PublishingContext

class User extends CatalogueElement {

    String username
    String password
    String email
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    String apiKey

    MaxActiveUsersService maxActiveUsersService

    static transients = ['maxActiveUsersService']

    static constraints = {
        username blank: false, unique: true, maxSize: 255
        password blank: false, maxSize: 255
        email    nullable: true, email: true, unique: true, maxSize: 255
        apiKey   nullable: true, maxSize: 255
        enabled validator: { val, obj, errors ->
            if ( maxActiveUsersService.maxActiveUsers() ) {
                errors.rejectValue('enabled', 'mc.max.active.users.limit.reached', [maxActiveUsersService.maxUsers] as Object[], "Limit of ${maxActiveUsersService.maxUsers} users has been reached")
                return
            }
            return true

        }
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
        if (!readyForQueries) {
            return []
        }
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    void beforeInsert() {
        super.beforeInsert()
        encodePassword()
        apiKey = ApiKeyUtils.apiKey()
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
