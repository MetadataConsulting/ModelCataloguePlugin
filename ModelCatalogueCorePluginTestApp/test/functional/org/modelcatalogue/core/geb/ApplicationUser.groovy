package org.modelcatalogue.core.geb

class ApplicationUser {

    final String username
    final String password

    private ApplicationUser(String username, String password) {
        this.username = username
        this.password = password
    }

    static ApplicationUser create(String username, String password = username) {
        return new ApplicationUser(username, password)
    }
}
