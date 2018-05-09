package org.modelcatalogue.core.geb

import geb.Page

class DataClassPage extends Page {

    static url = '/#'

    static at = { title.startsWith('History of') }

    static content = {
        editButton { $('a#role_item-detail_inline-editBtn') }
        saveButton(wait: false, required: false) { $('button#role_item-detail_inline-edit-submitBtn') }
//        discription(wait: false, required: false) { $('textarea') }
//        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        formMetadataLink { $('span.btn.btn-link btn-sm ng-binding') }
        tabs { $('ul.nav.nav-tabs a', text: it) }
        parentAddButton { $('span.fa.fa-plus-circle') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    void edit() {
        editButton.click()
    }

    void save() {
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
        parentAddButton[2].click()
    }

}
