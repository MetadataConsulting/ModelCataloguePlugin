package org.modelcatalogue.core

import org.modelcatalogue.core.security.User

public trait LogoutListeners {

    List<LogoutListener> listeners = []


    void addLogoutListener(LogoutListener listener) {
        listeners << listener
    }

    void userLoggedOut(User user) {
        for(LogoutListener listener in listeners) {
            listener.userLoggedOut(user)
        }
    }
}