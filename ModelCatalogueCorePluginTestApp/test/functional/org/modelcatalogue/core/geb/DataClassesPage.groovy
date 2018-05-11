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
        showMoreButton { $('span.fa-plus-square-o') }
        editDataClassButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
    }

    int count() {
        rows.size()
    }

    boolean isAddItemIconVisible() {
        if ( addItemIcon.empty ) {
            return false
        }
        true
    }

    void createDataClass() {
        createDateClassLink.click()
    }

    void showMore() {
        showMoreButton.click()
    }

    boolean editDataClassDisabled() {
        waitFor { editDataClassButton.@('disabled') }
    }

    void selectDataClass(String value) {
        rows.$('a', text: value).click()
    }
}
