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
            $("#add-import-menu-item")
        }
        checkImport(required: false) { $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center") }
        removeButton(wait: false) { $("a#role_item_remove-relationshipBtn") }
        expandButton(wait: true) { $("span.fa-plus-square-o") }
        dataModelDropDown(wait: true) { $("a#role_item_catalogue-element-menu-item-link") }
        dataModel(wait: true, required: false) { String value -> $('a.preserve-new-lines', text: value) }
    }

    void addItem() {
        dataModelDropDown.click()
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
        dataModel(value)?.displayed
    }
}
