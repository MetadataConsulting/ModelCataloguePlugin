package org.modelcatalogue.core.geb

import geb.Page

class ParentClassModalPage extends Page {

    static at = { title.contains("Parents of") }

    static content = {
        searchMoreButton(required: false, wait: true) { $('span.search-for-more-icon') }
        createRelationshipButton(required: false, wait: true) { $('button.btn-primary', type: 'submit') }
        cancelButton(required: true) { $('button.btn-warning') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void createRelationship() {
        createRelationshipButton.click()
    }

    void openModelHome(String modelname) {
        $('ul .catalogue-element-treeview-name', text: contains(modelname)).click()
    }

}
