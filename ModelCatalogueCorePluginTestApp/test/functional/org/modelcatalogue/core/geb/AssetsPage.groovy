package org.modelcatalogue.core.geb

import geb.Page

class AssetsPage extends Page {

    static url = '/#'

    static at = { title == 'Assets' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/assets/all" : ''
    }

    static content = {
        addItemIcon(required: false, wait: true) {
            $("#role_list_create-catalogue-element-menu-item-link")
        }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }

    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        deleteBttn.attr("class") == "disabled"
    }

    void dataElementDropDown() {
        dataElementDropDown.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }
}
