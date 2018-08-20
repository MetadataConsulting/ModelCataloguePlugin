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
        addBusinessRule { $('span.fa.fa-plus-circle.text-success') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDownTag { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
        businessElement { $('tr.inf-table-item-row td span a', text: contains(it),0) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    void addBusinessRuleClick() {
        addBusinessRule.click()
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

    boolean isBusinessElementVisible(String val) {
        businessElement(val).displayed
    }
}
