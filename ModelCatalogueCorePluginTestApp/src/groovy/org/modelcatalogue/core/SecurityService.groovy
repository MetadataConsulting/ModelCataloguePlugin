package org.modelcatalogue.core

import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User

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
    boolean hasRole(String role, DataModel dataModel)


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

    /**
     * Utility method to encode the password before being stored to the database.
     * The underlying service needs to take care of decoding/matching the password.
     *
     * Returns the encoded password or the password argument unchanged if the password encoding is not set up
     * @param password the password to be encoded
     * @return the encoded password or the password argument unchanged if the password encoding is not set up
     */
    String encodePassword(String password)

    /**
     * Retrieves the currently logged in user or null.
     * @return currently logged in user or null
     */
    User getCurrentUser()

    void addLogoutListener(LogoutListener listener)

    Map<String, Long> getUsersLastSeen()

    /**
     * Logs out user specified by username
     * @param username username of the user to be logged out
     */
    void logout(String username)

    /**
     * Returns true if the user is subscribed to a particular model or it doesn't matter if she has any role.
     *
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN. Any
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */
    boolean isSubscribed(DataModel dataModel)

    /**
     * Returns true if the user is subscribed to a particular model or it doesn't matter if she has any role.
     *
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN. Any
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */

    boolean isSubscribed(Set<Long> dataModelIds)



    /**
     * Returns true if the user is subscribed to a particular model or it doesn't matter if she has any role.
     *
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN. Any
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */
    boolean isSubscribed(CatalogueElement ce)


    /**
     * Returns set of models associated with a user
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */


    List<DataModel> getSubscribed()


    /**
     * Returns set of roles associated with a user based on the models they are subscribed to
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN, SUPERVISOR. Any
     * service implementations should map to these roles as well.
     *
     * @param role the role to be tested
     * @return true if the user has particular role or it doesn't matter if she has any role
     */

    Set getRoles(String dataModelId)

    /**
     * Adds a user with a particular role to a data model. This gives them access to the particular model
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN, SUPERVISOR. Any
     * service implementations should map to these roles as well.
     *
     * @param the user to be added, role the role to be added, data model that data model the user and role applies to
     */

    void addUserRoleModel(User user, Role role, DataModel model)


    /**
     * Removes a user with a particular role from a data model. This gives them access to the particular model
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN, SUPERVISOR. Any
     * service implementations should map to these roles as well.
     *
     * @param the role, the user and data model to be removed
     */
    void removeUserRoleModel(User user, Role role, DataModel model)


    /**
     * Removes all the userroles for a user for a data model. This removes all their access to a particular data model
     * useful when deleting a data model or revoking all access to a data model for a particular user
     * Following roles are supported by the core plugin: VIEWER, CURATOR, ADMIN, SUPERVISOR. Any
     * service implementations should map to these roles as well.
     *
     * @param the user and data model for all roles to be to be removed
     */

    void removeAllUserRoleModel(User user, DataModel model)

/**
 * Checks if the current user is a supervisor
 * @return true if the user has particular role or it doesn't matter if she has any role
 */
    boolean isSupervisor()


/**
 * Copies the user roles from one model to another.
 * Useful when creating a new model version
 */
    void copyUserRoles(DataModel sourceModel, DataModel destinationModel)
}
