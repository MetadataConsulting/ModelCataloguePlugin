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
        dataTypeByName(required: false, wait: true) { $('a', text: it) }
        dataTypeByName(required: false, wait: true) { $('a', text: it) }
        createDataTypePlusButton { $('table tr.inf-table-footer-action span.fa-plus-circle') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        deleteBttn.attr("class") == "disabled"
    }

    void dataElementDropDown() {
        dataElementDropDown.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
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

    void createDataTypeFromPlusButton() {
        createDataTypePlusButton.click()
    }

    boolean hasDataType(String name) {
        if (dataTypeByName(name)) {
            return true
        }
        false
    }

    boolean containsDataTypeByName(String value) {
        $('a', text: value).displayed
    }

    void selectDataType(String value) {
        dataTypeByName(value).click()
    }
}
