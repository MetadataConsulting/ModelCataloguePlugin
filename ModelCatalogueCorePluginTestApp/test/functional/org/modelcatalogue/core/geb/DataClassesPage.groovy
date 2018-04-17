package org.modelcatalogue.core.geb

import geb.Page

class DataClassesPage extends Page implements InputUtils, MetadataUtils {

    static url = '/#'

    static at = { title == 'Data Classes' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataClasses/all" : ''
    }

    static content = {
        createDateClassLink(required: false, wait: true) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        rows(required: false, wait: true) { $('div.inf-table-body tbody tr') }

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

    void selectDataClassByName(String value) {
        rows.$('a', text: value).click()
    }

}
