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
        addItemIcon(required: false, wait: true) {
            $("#role_list_create-catalogue-element-menu-item-link")
        }
        showMoreButton { $('span.fa-plus-square-o') }
        editDataTypeButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
        dataTypeByName(required: false, wait: true) { $('a', text: it) }
        createDataTypePlusButton { $('a#role_list_create-catalogue-element-menu-item-link') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDownTag { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        deleteBttn.attr("class") == "disabled"
    }

    void dataElementDropDown() {
        dataElementDropDownTag.click()
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
        sleep(2_000)
        dataTypeByName(value).click()
    }

    void showMore() {
        showMoreButton.click()
    }

    boolean editDataTypeDisabled() {
        waitFor { editDataTypeButton.@('disabled') }
    }


    void createDataTypeFromGreenPlusButton() {
        addItemIcon.click()
    }

//    boolean hasDataType(String name) {
//        rows.$('a', text: name).displayed
//    }
}
