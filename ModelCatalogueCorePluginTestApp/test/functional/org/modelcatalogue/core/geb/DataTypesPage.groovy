package org.modelcatalogue.core.geb

import geb.Page

class DataTypesPage extends Page {

    static url = '/#'

    static at = { title == 'Data Types' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataTypes/all" : ''
    }

    static content = {
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        createDateTypeLink(required: false) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        rows { $('div.inf-table-body tbody tr') }
        addItemIcon(required: false) { $("tfoot span.fa-plus-circle") }
    }

    boolean isAddItemIconVisible() {
        if ( addItemIcon.empty ) {
            return false
        }
        true
    }

    boolean areCreateButtonsVisible() {
        if (createDateTypeLink.empty) {
            return false
        }
        if (addItemIcon.empty) {
            return false
        }
        true
    }

    int count() {
        rows.size()
    }

    void createDataTypeFromNavigation() {
        createDateTypeLink.click()
    }

    void createDataTypeFromGreenPlusButton() {
        addItemIcon.click()
    }

    boolean hasDataType(String name) {
        rows.$('a', text: name).displayed
    }
}
