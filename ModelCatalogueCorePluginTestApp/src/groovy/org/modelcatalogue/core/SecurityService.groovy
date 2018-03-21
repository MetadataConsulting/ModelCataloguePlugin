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
}
