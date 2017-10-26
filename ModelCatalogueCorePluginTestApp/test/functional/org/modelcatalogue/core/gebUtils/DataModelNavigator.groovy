package org.modelcatalogue.core.gebUtils

class DataModelNavigator {

    final AbstractModelCatalogueGebSpec spec

    DataModelNavigator(AbstractModelCatalogueGebSpec spec) {
        this.spec = spec
    }

    /**
     * Selects a tree node and ensures it is opened.
     */
    DataModelNavigator open(String node) {
        spec.selectInTree(node, true);
        return this
    }

    /**
     * Selects a tree node nevertheless it is possible to open.
     */
    DataModelNavigator select(String node) {
        spec.selectInTree(node);
        return this
    }
}
