package org.modelcatalogue.core

import org.modelcatalogue.core.security.User

class UserController extends AbstractExtendibleElementController<User> {

    def dataArchitectService

    UserController() {
        super(User, false)
    }

    @Override
    protected String getRoleForSaveAndEdit() { "ADMIN" }
}
