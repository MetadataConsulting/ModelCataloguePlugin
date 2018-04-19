package org.modelcatalogue.core.geb

import geb.Page

class UserSearchPage extends Page implements InputUtils {

    static url = '/userAdmin'

    static at = { title == 'User Search' }

    static content = {
        username(wait: true) { $('input#username', 0) }
        searchButton(wait: true) { $('a#search') }
        usersList(required: false, wait: true) { $('.list tbody tr') }
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
