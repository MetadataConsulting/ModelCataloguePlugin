package org.modelcatalogue.core.geb

import geb.Page

class BusinessRulesPage extends Page {

    static url = '/#'

    static at = { title == 'Business Rules' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/validationRule/all" : ''
    }

    static content = {
        addItemIcon(required: false, wait: true) {
            $("#role_list_create-catalogue-element-menu-item-link")
        }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDownTag { $('button#role_item_catalogue-elementBtn') }
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
        dataElementDropDownTag.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }
}
