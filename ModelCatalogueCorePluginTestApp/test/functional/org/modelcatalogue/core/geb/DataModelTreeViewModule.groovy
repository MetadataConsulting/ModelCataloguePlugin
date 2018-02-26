package org.modelcatalogue.core.geb

import geb.Module

class DataModelTreeViewModule extends Module {

    static content = {
        item { $('.catalogue-element-treeview-name', text: it) }
    }

    void select(String name) {
        item(name).click()
    }
}
