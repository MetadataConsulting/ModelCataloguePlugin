package org.modelcatalogue.core.security

import groovy.util.logging.Slf4j
import org.modelcatalogue.core.persistence.UserGormService

@Slf4j
class UserService {

    UserGormService userGormService

    public static final String ENV_SUPERVISOR_EMAIL = 'MC_SUPERVISOR_EMAIL'

    String findApiKeyByUsername(String username, boolean regenerate = true) {
        String apiKey = userGormService.findApiKeyByUsername(username)
        if ( !apiKey || regenerate ) {
            apiKey = ApiKeyUtils.apiKey()
            userGormService.updateApiKeyByUsername(username, apiKey)
        }
        apiKey
    }
}
