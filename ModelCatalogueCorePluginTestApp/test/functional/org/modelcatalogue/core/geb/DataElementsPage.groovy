package org.modelcatalogue.core.geb

import geb.Page

class DataElementsPage extends Page {

    static url = '/#'

    static at = { title == 'Data Elements' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataElement/all" : ''
    }

    static content = {
        createDateElementLink(required: false) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        anchorElements { $("td.col-md-4>span>span>a") }
        dataElements { $("td.col-md-4>span>span>a", text: it) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        addItemIcon(required: false, wait: true) {
            $("#role_list_create-catalogue-element-menu-item-link")
        }
        firstRowLink { $('tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
        rows { $('div.inf-table-body tbody tr') }
        elementByName(wait: true) { $('.inf-table-item-cell a', text: contains(it)) }
        showMoreButton { $('span.fa-plus-square-o') }
        editDataElementButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
        elementsList { $('table.inf-table tbody tr') }
        settings(wait: true) { $('#role_navigation-right_admin-menu-menu-item-link') }
        mappingUtilityTag(wait: true) { $('#action-batches-menu-item-link') }
        tagPlus { $('.btn.ng-scope.fa.fa-plus-circle.text-success') }
        tagName { $('a.preserve-new-lines') }
    }

    void createDataElement() {
        createDateElementLink.click()
    }

    void createTagRelationShip() {
        tagPlus.click()
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        deleteBttn.attr("class") == "disabled"
    }

    void selectDataElementDropDown() {
        dataElementDropDown.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }

    boolean iscreateDateElementLinkVisible() {
        if (createDateElementLink.empty) {
            return false
        }
        true
    }

    void selectRow(int row) {
        anchorElements[row].click()
    }

    void selectdataElements(String value) {
        dataElements(value).click()
    }

    String dataElementCreated() {
        firstRowLink.text()
    }

    boolean hasDataElement(String name) {
        elementByName(name).displayed
    }

    void showMore() {
        showMoreButton.click()
    }

    boolean editDataElementDisabled() {
        waitFor { editDataElementButton.@('disabled') }
    }

    void selectDataElement(String value) {
        rows.$('a', text: value).click()
    }

    boolean containsElement(String value) {
        elementsList.$('a', text: value).displayed
    }

    void mappingUtility() {
        settings.click()
        mappingUtilityTag.click()
    }

    boolean displayTagName(String name) {
        tagName.text().contains(name)
    }
}
