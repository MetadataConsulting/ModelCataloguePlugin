package org.modelcatalogue.core.geb

import geb.Page

class ParentClassModalPage extends Page {

    static at = { title.contains("Parents of") }

    static content = {
        searchMoreButton(required: false, wait: true) { $('span.search-for-more-icon') }
        createRelationshipButton(required: false, wait: true) { $('button.btn-primary', type: 'submit') }
        cancelButton(required: true) { $('button.btn-warning') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        modelHome(wait: true) { String name ->
            $('ul .catalogue-element-treeview-name', text: contains(name)).click()
        }
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void createRelationship() {
        sleep(3_000)
        createRelationshipButton.click()
        sleep(2_000)

    }

    void openModelHome(String modelname) {
        sleep(2_000)
        modelHome(modelname)
    }

}
