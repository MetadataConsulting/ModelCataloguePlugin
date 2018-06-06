package org.modelcatalogue.core.geb

import geb.Page

class DataImportsPage extends Page {

    static url = '/#'

    static at = { title.contains("Imports") }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataElement/all" : ''
    }

    static content = {
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        checkImport(required: false) { $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center") }
        removeButton(wait: false) { $("a#role_item_remove-relationshipBtn") }
        expandButton(wait: true) { $("span.fa-plus-square-o") }
    }

    void addItem() {
        addItemIcon.click()
    }

    void expandTag() {
        expandButton.click()
    }

    boolean isRemoveButtonVisible() {
        if (removeButton.empty) {
            return false
        }
        return true
    }

    void remove() {
        removeButton.click()
    }

    Boolean containsDataModel(String value) {
        waitFor { addItemIcon }
        $('a', text: value).displayed
    }

    boolean containsData(String value) {
        $('a', text: value).displayed
    }
}
