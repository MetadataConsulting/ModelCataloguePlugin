package org.modelcatalogue.core

class DataModelNavigator {

    final AbstractModelCatalogueGebSpec spec

    DataModelNavigator(AbstractModelCatalogueGebSpec spec) {
        this.spec = spec
    }

    DataModelNavigator rightShift(String node) {
        spec.selectInTree(node);
        return this
    }
}
