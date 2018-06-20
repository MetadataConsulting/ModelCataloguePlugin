package org.modelcatalogue.core.geb

import geb.Page

class DataClassesPage extends Page {

    static url = '/#'

    static at = { title == 'Data Classes' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataClasses/all" : ''
    }

    static content = {
        createDateClassLink(required: false) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        rows { $('div.inf-table-body tbody tr') }
        nav { $('div.navbar-collapse', 0).module(NavModuleAdmin) }
        selectDataClass(wait: true) { $('span.ng-binding a', text: it) }
        titlename { $('div.col-md-12 h3') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDownTag { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
        dataClassByName(wait: true) { rows.$('a', text: it) }
        showMoreButton { $('span.fa-plus-square-o') }
        editDataClassButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
        titlename { $('div   h3') }
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

    int count() {
        rows.size()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }

    void createDataClass() {
        createDateClassLink.click()
    }

    /*void selectDataClass(String value) {
        rows.$('a', text: value).click()
    }*/

    Boolean dataClassPresent(String value) {
        waitFor { createDateClassLink }
        $('a', text: value).displayed
    }

    void selectDataClassLink(String value) {
        selectDataClass(value).click()
    }

    String titleText() {
        return titlename.text()
    }

    void findByName(String value) {
        $('a', text: value).click()
    }

    void selectDataClassByName(String value) {
        dataClassByName(value).click()
        sleep(2_000)
    }

    boolean containsDataClass(String value) {
        if ($('a', text: value)) {
            return true
        }
        false
    }


    void showMore() {
        showMoreButton.click()
    }

    boolean editDataClassDisabled() {
        waitFor { editDataClassButton.@('disabled') }
    }

    boolean containsDataClass(String value) {
        rows.$('a', text: value).displayed
    }

//    void selectDataClass(String value) {
//        rows.$('a', text: value).click()
//    }

    String titleText() {
        return titlename.text()
    }
}
