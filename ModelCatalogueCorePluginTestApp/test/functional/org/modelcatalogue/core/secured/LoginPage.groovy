package org.modelcatalogue.core.secured

import geb.Page

class LoginPage extends Page {

    static url = '/login/auth'
    static at = { title == 'Login' }
}
