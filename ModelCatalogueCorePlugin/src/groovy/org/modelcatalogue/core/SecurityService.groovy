package org.modelcatalogue.core

/**
 * Lightweight abstraction of security.
 */
public interface SecurityService {

    /**
     * Returns true if the user is logged in or if it doesn't matter if she is logged in.
     * @return true if the user is logged in or if it doesn't matter if she is logged in
     */
    boolean isUserLoggedIn()

    /**
     * Returns true if the user has particular role or it doesn't matter if she has any role.
     *
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN. Any
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */
    boolean hasRole(String role)
}