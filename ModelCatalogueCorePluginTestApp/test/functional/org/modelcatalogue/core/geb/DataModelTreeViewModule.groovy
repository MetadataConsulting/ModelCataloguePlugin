package org.modelcatalogue.core.geb

import geb.Module

class DataModelTreeViewModule extends Module {

    static content = {
        dataModelLink { $('a.catalogue-element-treeview-icon') }
        item { $('ul .catalogue-element-treeview-name', text: it) }
    }

    void dataModel() {
        dataModelLink.click()
    }

    void select(String name) {
        item(name).click()
        sleep(2_000)
    }
}
