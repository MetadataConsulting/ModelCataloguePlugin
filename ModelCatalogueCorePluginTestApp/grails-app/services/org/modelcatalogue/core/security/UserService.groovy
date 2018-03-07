package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.persistence.UserGormService

@Slf4j
class UserService {

    UserGormService userGormService

    public static final String ENV_SUPERVISOR_EMAIL = 'MC_SUPERVISOR_EMAIL'
    public static final String ENV_INVITATION_EMAIL_SUBJECT = 'MC_INVITATION_EMAIL_SUBJECT'
    public static final String ENV_INVITATION_EMAIL_BODY = 'MC_INVITATION_EMAIL_BODY'

    def mailService

    @CompileStatic
    @Transactional
    User resetApiKey(String username) {
        User user = userGormService.queryByUsername(username).get()
        if ( !user ) {
            log.error 'Unable to find User by username: {}', username
            return null
        }
        user.apiKey = ApiKeyUtils.apiKey()
        FriendlyErrors.failFriendlySave(user)
    }

    String findApiKeyByUsername(String username, boolean regenerate = true) {
        String apiKey = userGormService.findApiKeyByUsername(username)
        if ( !apiKey || regenerate ) {
            apiKey = ApiKeyUtils.apiKey()
            userGormService.updateApiKeyByUsername(username, apiKey)
        }
        apiKey
    }
}
