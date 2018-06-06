package org.modelcatalogue.core.geb

import geb.Page

class DataClassPage extends Page {

    static url = '/#'

    static at = { title.startsWith('History of') }

    static content = {
        editButton { $('a#role_item-detail_inline-editBtn') }
        saveButton(wait: false, required: false) { $('button#role_item-detail_inline-edit-submitBtn') }
//        discription(wait: false, required: false) { $('textarea') }
//        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        formMetadataLink { $('span.btn.btn-link btn-sm ng-binding') }
        tabs { $('ul.nav.nav-tabs a', text: it) }
        parentAddButton { $('span.fa.fa-plus-circle') }
        dataClassDropDown { $('#role_item_catalogue-element-menu-item-link') }
        createRelationship { $('#create-new-relationship-menu-item-link') }
        checkMinMaxOccur { $('tr.inf-table-item-row.warning td.inf-table-item-cell.ng-scope.col-md-2') }
        dataClassDropdownLink { $('#role_item_catalogue-element-menu-item-link') }
        deleteDataClass { $('#delete-menu-item-link') }
        confirmDataClassDelete(required: false, wait: true) { $('button.btn-primary', text: "OK") }
        dataClassMenu { $('#role_item_catalogue-element-menu-item-link') }
        dataClassMenuDropdown { $('#role_item_catalogue-element-menu-item ul.dropdown-menu li') }
        historyList(required: false, wait: true) { $('div#history-changes tbody tr') }
        className { $('h3.ce-name input', 0) }
    }

    void edit() {
        editButton.click()
    }

    String occuranceStatus() {
        return checkMinMaxOccur.text()

    }

    void dataClassDropDownClick() {
        dataClassDropDown.click()
    }

    void createRelationshipClick() {
        createRelationship.click()
    }

    void save() {
        saveButton.click()
    }

    /*void writeDiscription(String text) {
        discription.value(text)
    }*/

    void formMetadata() {
        formMetadataLink.click()
    }

    void selectParents() {
        tabs("Parents").click()
    }

    void addParent() {
        parentAddButton[2].click()
    }

    void dataClassDropdown() {
        dataClassDropdownLink.click()
    }

    void deleteDataClass() {
        deleteDataClass.click()
    }

    void confirmDelete() {
        waitFor { confirmDataClassDelete }
        confirmDataClassDelete.click()
    }

    void selectDataClassDropdown() {
        dataClassMenu.click()
    }

    void selectCreateRelationship() {
        $('a#create-new-relationship-menu-item-link').click()
    }

    String historyChange(int row) {
        historyList[row].$('td', 3).text()
    }

    void editClassName(String value) {
        className.value(value)
    }

}
