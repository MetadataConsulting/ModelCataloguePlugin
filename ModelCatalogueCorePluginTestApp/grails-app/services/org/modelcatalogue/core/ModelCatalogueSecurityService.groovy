package org.modelcatalogue.core

import org.modelcatalogue.core.security.User

/**
 * Default implementation meaning no security. The user is always logged in and has all the roles.
 */
class ModelCatalogueSecurityService implements SecurityService, LogoutListeners {

    static transactional = false

    @Override
    boolean isUserLoggedIn() {
        return true
    }

    @Override
    boolean hasRole(String role) {
        return true
    }

    @Override
    boolean hasRole(String role, DataModel dataModel) {
        return true
    }

    @Override
    String encodePassword(String password) {
        return password
    }

    @Override
    User getCurrentUser() {
        return null
    }

    @Override
    Map<String, Long> getUsersLastSeen() {
        return [:]
    }

    @Override
    void logout(String username) {}
}
