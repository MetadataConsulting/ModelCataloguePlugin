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

    void dataClasses() {
        select('Data Classes')
    }

    void dataElements() {
        select('Data Elements')
    }

    void dataTypes() {
        select('Data Types')
    }

    void measurementUnits() {
        select('Measurement Units')
    }

    void businessRules() {
        select('Business Rules')
    }

    void tags() {
        select('Tags')
    }

    void deprecatedItems() {
        select('Deprecated Items')
    }

    void importedDataModels() {
        select('Imported Data Models')
    }

    void versions() {
        select('Versions')
    }

    void select(String name) {
        item(name).click()
        sleep(2000)
    }
}
