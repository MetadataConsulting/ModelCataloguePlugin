package org.modelcatalogue.core.geb

import geb.Page

class UserSearchPage extends Page implements InputUtils {

    static url = '/userAdmin'

    static at = { title == 'User Search' }

    static content = {
        username(required: true) { $('input#username') }
        searchButton(required: true) { $('a#search') }
        usersList { $('.list tbody tr') }
    }

    void fillUser(String value) {
        fillInput(username, value)
    }

    void search() {
        searchButton.click()
    }

    void selectUser(String value) {
        usersList[0].$('a', 0).click()
    }

}
