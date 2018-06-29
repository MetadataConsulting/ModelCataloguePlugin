package org.modelcatalogue.core.geb

import geb.Page

class DataElementPage extends Page {

    static url = '/#'
    static at = { dataElementDropdown.displayed }

    @Override
    String convertToPath(Object[] args) {
        args?.size() >= 2 ? "/${args[0]}/dataElement/${args[1]}" : ''
    }

    static content = {
        editButton(required: false) { $("a#role_item-detail_inline-editBtn", 0) }
        submitButton(required: false) { $("button#role_item-detail_inline-edit-submitBtn", 0) }
        descriptionTextarea(required: false) {
            $("#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div > div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > div:nth-child(4) > div > ng-include > div > div > span > div > textarea", 0)
        }
        dataTypeInput(required: false) { $("input#dataType", 0) }
        unitNameSpan(required: false) { $('span.unit-name', 0) }
        dataElementDropdown { $('#role_item_catalogue-element-menu-item-link') }
        deleteDataElementLink { $('#delete-menu-item-link') }
        confirmDeleteButton(wait: true, required: false) { $('form button.btn-primary', text: "OK") }
        dataTypeList(required: false, wait: true) { $('a.small.with-pointer.ng-scope') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    String getUnitName() {
        if ( unitNameSpan.isDisplayed() ) {
            return unitNameSpan.text()
        }
        null
    }

    void edit() {
        editButton.click()
    }

    void setDataType(String value) {
        for ( char c : value.toCharArray() ) {
            dataTypeInput << "${c}".toString()
        }
    }

    void setDescription(String description) {
        if ( descriptionTextarea.isDisplayed() ) {
            descriptionTextarea << description
        }
    }

    void submit() {
        if ( submitButton.isDisplayed() ) {
            submitButton.click()
        }
    }

    boolean editDataElementDisabled() {
        waitFor { editButton.@('disabled') }
    }

    void dataElementDropdown() {
        dataElementDropdown.click()
    }

    void deleteDataElement() {
        deleteDataElementLink.click()
    }

    void confirmDelete() {
        waitFor { confirmDeleteButton.click() }
    }

    boolean containsDataType(String name) {
        dataTypeList.text().contains(name)
    }
}
