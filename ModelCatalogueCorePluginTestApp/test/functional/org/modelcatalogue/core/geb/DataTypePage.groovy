package org.modelcatalogue.core.geb

import geb.Page

class DataTypePage extends Page {

    static url = '/#'
    static at = { dataTypeDropdown.displayed }

    static content = {
        editButton(required: false) { $("a#role_item-detail_edit-catalogue-elementBtn", 0) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        dataTypeDropdown { $('#role_item_catalogue-element-menu-item-link') }
    }


    void edit() {
        editButton.click()
    }

    boolean editDataTypeDisabled() {
        waitFor { editButton.@('disabled') }
    }
}
