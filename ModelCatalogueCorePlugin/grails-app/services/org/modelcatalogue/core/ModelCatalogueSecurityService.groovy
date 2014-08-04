package org.modelcatalogue.core

/**
 * Default implementation meaning no security. The user is always logged in and has all the roles.
 */
class ModelCatalogueSecurityService implements SecurityService {

    @Override
    boolean isUserLoggedIn() {
        return true
    }

    @Override
    boolean hasRole(String role) {
        return true
    }
}
