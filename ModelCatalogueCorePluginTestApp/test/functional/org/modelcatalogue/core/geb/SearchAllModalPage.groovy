package org.modelcatalogue.core.geb

import geb.Page

class SearchAllModalPage extends Page implements InputUtils {
    static at = { $('.modal-dialog', 0).text().contains('Showing all results. ') }

    static content = {
        searchBar(required: false, wait: true) { $('input#value') }
        searchMoreModalsList(wait: true) { $('h4.list-group-item-heading') }
        searchList(wait: true) { $('h4.list-group-item-heading', text: contains(it)) }
    }

    void searchDataType(String value) {
        fillInput(searchBar, value)
    }

    void clearSearchField() {
        searchBar.value("")
    }

    void selectDataType() {
        searchMoreModalsList[0].click()
    }

    Boolean selectDataTypeText(String dataTypeName, String dataModelVersion) {
        String text = searchList(dataTypeName).text()
        text.contains(dataTypeName) && text.contains(dataModelVersion)
    }

    boolean noResultFound() {
        if ($('span.ng-scope', text: "No Results")) {
            return true
        }
        false
    }
}
