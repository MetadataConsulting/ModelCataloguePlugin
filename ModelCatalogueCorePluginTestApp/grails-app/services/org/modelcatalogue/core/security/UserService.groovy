package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.util.FriendlyErrors

@Slf4j
class UserService {

    public static final String ACCESS_LEVEL_SUPERVISOR = 'supervisor'
    public static final String ACCESS_LEVEL_ADMIN = 'admin'
    public static final String ACCESS_LEVEL_CURATOR = 'curator'
    public static final String ACCESS_LEVEL_VIEWER = 'viewer'
    public static final String ACCESS_LEVEL_GUEST = 'guest'

    public static final String ROLE_SUPERVISOR = 'ROLE_SUPERVISOR'
    public static final String ROLE_ADMIN = 'ROLE_ADMIN'
    public static final String ROLE_CURATOR = 'ROLE_METADATA_CURATOR'
    public static final String ROLE_USER = 'ROLE_USER'

    public static final String ENV_ADMIN_EMAIL = 'MC_ADMIN_EMAIL'
    public static final String ENV_SUPERVISOR_EMAIL = 'MC_SUPERVISOR_EMAIL'
    public static final String ENV_INVITATION_EMAIL_SUBJECT = 'MC_INVITATION_EMAIL_SUBJECT'
    public static final String ENV_INVITATION_EMAIL_BODY = 'MC_INVITATION_EMAIL_BODY'

    def mailService

    void redefineRoles(User user, String accessLevel) {
        switch (accessLevel) {
            case ACCESS_LEVEL_SUPERVISOR: createRoleIfMissing(user, ROLE_SUPERVISOR)
            case ACCESS_LEVEL_ADMIN: createRoleIfMissing(user, ROLE_ADMIN)
            case ACCESS_LEVEL_CURATOR: createRoleIfMissing(user, ROLE_CURATOR)
            case ACCESS_LEVEL_VIEWER: createRoleIfMissing(user, ROLE_USER)
        }

        switch (accessLevel) {
            case ACCESS_LEVEL_GUEST: removeExistingRole(user, ROLE_USER)
            case ACCESS_LEVEL_VIEWER: removeExistingRole(user, ROLE_CURATOR)
            case ACCESS_LEVEL_CURATOR: removeExistingRole(user, ROLE_ADMIN)
            case ACCESS_LEVEL_ADMIN: removeExistingRole(user, ROLE_SUPERVISOR)
        }
    }

    void inviteAdmins() {
        String adminEmail = System.getenv(ENV_ADMIN_EMAIL)
        String supervisorEmail = System.getenv(ENV_SUPERVISOR_EMAIL)
        String invitationEmailSubject = System.getenv(ENV_INVITATION_EMAIL_SUBJECT)
        String invitationEmailBody = System.getenv(ENV_INVITATION_EMAIL_BODY)

        if(!adminEmail || !supervisorEmail || !invitationEmailBody) {
            return
        }

        User admin = User.findByEmail(adminEmail)

        if (admin) {
            return
        }

        mailService.sendMail {
            to adminEmail
            from supervisorEmail
            subject(invitationEmailSubject ?: "Your application is ready")
            html invitationEmailBody
        }

        mailService.sendMail {
            to supervisorEmail
            from supervisorEmail
            subject(invitationEmailSubject ?: "New application is ready for your customer")
            html invitationEmailBody.replace(adminEmail, supervisorEmail)
        }
    }

    private static void createRoleIfMissing(User user, String authority) {
        UserRole.create(user, Role.findByAuthority(authority), true)
    }

    private static void removeExistingRole(User user, String authority) {
        UserRole.remove(user, Role.findByAuthority(authority), true)
    }

    protected DetachedCriteria<User> findQueryByUsername(String usernameParam) {
        DetachedCriteria<User> q = User.where { username == usernameParam }
    }

    @CompileStatic
    @Transactional
    void resetApiKey(String username) {
        User user = findQueryByUsername(username).get()
        if ( !user ) {
            log.error 'Unable to find User by username: {}', username
            return
        }
        user.apiKey = ApiKeyUtils.apiKey()
        FriendlyErrors.failFriendlySave(user)
    }

    @CompileDynamic
    @Transactional
    String findApiKeyByUsername(String username, boolean generateIfNonExisting = true) {
        String apiKey = findQueryByUsername(username).projections {
            property('apiKey')
        }.get()
        if ( !apiKey && generateIfNonExisting ) {
            resetApiKey(username)
            apiKey = findApiKeyByUsername(username, false)
        }
        apiKey
    }
}
