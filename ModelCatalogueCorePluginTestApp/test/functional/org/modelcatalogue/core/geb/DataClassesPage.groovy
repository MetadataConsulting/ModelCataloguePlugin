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
        titlename { $('div.col-md-12 h3') }
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

    String titleText() {
        return titlename.text()
    }
}
