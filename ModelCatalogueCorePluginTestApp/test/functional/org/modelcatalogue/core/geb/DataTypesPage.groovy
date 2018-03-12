package org.modelcatalogue.core.geb

import geb.Page

class DataTypesPage extends Page {

    static url = '/#'

    static at = { title == 'Data Types' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataTypes/all" : ''
    }

    static content = {
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }
}
