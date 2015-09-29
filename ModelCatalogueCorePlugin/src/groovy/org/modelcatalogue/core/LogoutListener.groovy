package org.modelcatalogue.core

import org.modelcatalogue.core.security.User

public interface LogoutListener {

    void userLoggedOut(User user)

}