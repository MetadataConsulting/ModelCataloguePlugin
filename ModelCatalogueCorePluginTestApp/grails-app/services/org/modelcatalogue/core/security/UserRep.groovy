package org.modelcatalogue.core.security


enum UserRep {

    SUPERVISOR( 'supervisor', System.getenv('MC_SUPERVISOR_PASSWORD') ?: 'supervisor', System.getenv(UserService.ENV_SUPERVISOR_EMAIL), 'supervisorabcdef123456'),
    USER( 'user', 'user', '', 'viewerabcdef123456'),
    CURATOR( 'curator', 'curator', '', 'curatorabcdef123456'),
    ADMIN( 'admin', 'admin', '', 'adminabcdef123456')

    String username
    String password
    String email
    String apiKey

    UserRep(String username, String password, String email, String apiKey) {
        this.username = username
        this.password = password
        this.email = email
        this.apiKey = apiKey
    }
}
