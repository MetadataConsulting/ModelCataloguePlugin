package org.modelcatalogue.core.security

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
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

    String apiKey

    static constraints = {
        username blank: false, unique: true, maxSize: 255
        password blank: false, maxSize: 255
        email    nullable: true, email: true, unique: true, maxSize: 255
        apiKey   nullable: true, maxSize: 255
        enabled validator: { val, obj, errors ->
            GrailsApplication grailsApplication = Holders.grailsApplication
            if (!val || !grailsApplication.config.mc.max.active.users) {
                return true
            }

            Integer maxUsers = grailsApplication.config.mc.max.active.users as Integer

            Integer numOfUsers = User.withNewSession {
                Role supervisorRole = Role.findByAuthority(UserService.ROLE_SUPERVISOR)

                if (!supervisorRole) {
                    return User.countByEnabled(true)
                }

                List<UserRole> supervisorsUserRole = UserRole.findAllByRole(supervisorRole)

                if (supervisorsUserRole) {
                    return User.countByEnabledAndUsernameNotInList(true, supervisorsUserRole*.user*.username)
                }

                return User.countByEnabled(true)
            }

            if (numOfUsers >= maxUsers) {
                errors.rejectValue('enabled', 'mc.max.active.users.limit.reached', [maxUsers] as Object[], "Limit of $maxUsers users has been reached")
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
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        return [:]
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
