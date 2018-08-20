package org.modelcatalogue.core.geb

import geb.Page

class DataTypePage extends Page {

    static url = '/#'
    static at = { dataTypeDropdown.displayed }

    static content = {
        editButton(required: false) { $("a#role_item-detail_edit-catalogue-elementBtn", 0) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        dataTypeDropdown { $('#role_item_catalogue-element-menu-item-link') }
        enumeratedTypeDropdown {
            $('a#role_item_catalogue-element-menu-item-link')
        }
        validateValueLink { $('a#validate-value-menu-item-link') }
        checkForDataType(wait: true, required: false) { $('h3.ce-name span', text: it) }
        deprecate(wait: true, required: false) { $('a#archive-menu-item-link span', 2) }
        confirmDeprecateTag(wait: true, required: false) { $('button.btn', 0) }
    }


    void edit() {
        editButton.click()
    }

    void confirmDeprecate() {
        confirmDeprecateTag.click()
    }

    void selectDataTypeDropdown() {
        dataTypeDropdown.click()
    }

    void clickDeprecate() {
        deprecate.click()
    }

    boolean editDataTypeDisabled() {
        waitFor { editButton.@('disabled') }
    }

    void enumeratedType() {
        enumeratedTypeDropdown.click()
    }

    void validateValue() {
        validateValueLink.click()
    }

    boolean isDataTypePageFor(String value) {
        sleep(2_000)
        checkForDataType(value)?.displayed
    }

}