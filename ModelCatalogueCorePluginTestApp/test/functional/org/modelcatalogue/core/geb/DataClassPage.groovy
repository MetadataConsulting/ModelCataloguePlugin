package org.modelcatalogue.core.geb

import geb.Page

class DataClassPage extends Page implements InputUtils {

    static url = '/#'

    static at = { title.startsWith('History of') }

    static content = {
        editButton { $('a#role_item-detail_inline-editBtn') }
        editButtonDissable { $('a#role_item-detail_edit-catalogue-elementBtn') }
        saveButton(wait: false, required: false) { $('button#role_item-detail_inline-edit-submitBtn') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        formMetadataLink { $('span.btn.btn-link.btn-sm.ng-binding', text: 'Form Metadata') }
        tabs { $('ul.nav.nav-tabs a', text: it) }
        parentAddButton(wait: true) { $('td span.fa.fa-plus-circle') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        dataClassDropDown { $('#role_item_catalogue-element-menu-item-link') }
        createRelationship { $('#create-new-relationship-menu-item-link') }
        checkMinMaxOccur { $('tr.inf-table-item-row.warning td.inf-table-item-cell.ng-scope.col-md-2') }
        dataClassDropdownLink { $('#role_item_catalogue-element-menu-item-link') }
        deleteDataClassTag { $('#delete-menu-item-link') }
        confirmDataClassDelete(required: false, wait: true) { $('button.btn-primary', text: "OK") }
        dataClassMenu { $('#role_item_catalogue-element-menu-item-link') }
        dataClassMenuDropdown { $('#role_item_catalogue-element-menu-item ul.dropdown-menu li') }
        historyList(required: false, wait: true) { $('div#history-changes tbody tr') }
        className(wait: true) { $('h3.ce-name input', 0) }
        newClassName(wait: true) { $('h3 span', 0) }
        cloneIntoAnotherTag(wait: true) { $('a#clone-menu-item-link') }
        seachMoreTag(wait: true) { $('span.input-group-addon.search-for-more-icon') }
        seachMoreListTag(wait: true) { $('a.list-group-item', text: contains(it)) }
        finishBttn(wait: true) { $('button.btn.btn-primary') }
        searchBar(wait: true) { $('#value') }
        titlename { $('div.col-md-12 h3') }

    }

    void edit() {
        editButton.click()
    }

    void seachMore() {
        seachMoreTag.click()
    }

    void finishButton() {
        finishBttn.click()
    }

    void seachMoreList(String val) {
        sleep(2_000)
        seachMoreListTag(val).click()
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
        sleep(2_000)
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
        parentAddButton.click()
    }

    boolean editDataClassDisabled() {
        waitFor { editButtonDissable.@('disabled') }
    }

    void dataClassDropdown() {
        dataClassDropdownLink.click()
    }

    void deleteDataClass() {
        deleteDataClassTag.click()
    }

    void confirmDelete() {
        waitFor { confirmDataClassDelete }
        confirmDataClassDelete.click()
    }

    void selectDataClassDropdown() {
        dataClassMenu.click()
    }

    void cloneIntoAnother() {
        cloneIntoAnotherTag.click()
    }

    void selectCreateRelationship() {
        $('a#create-new-relationship-menu-item-link').click()
    }

    String historyChange(int row) {
        historyList[row].$('td', 3).text()
    }

    void editClassName(String value) {
        className.value("")
        fillInput(className, value)
    }

    void searchBarFill(String value) {
        fillInput(searchBar, value)
    }

    Boolean checkClassName(String value) {
        newClassName.text().equals(value)
    }

    Boolean matchTitleText(String val) {
        titlename.text().contains(val)
    }

}
