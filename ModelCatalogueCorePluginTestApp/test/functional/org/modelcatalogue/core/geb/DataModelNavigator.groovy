package org.modelcatalogue.core.geb

class DataModelNavigator {

    final AbstractModelCatalogueGebSpec spec

    DataModelNavigator(AbstractModelCatalogueGebSpec spec) {
        this.spec = spec
    }

    DataModelNavigator rightShift(String node) {
        spec.selectInTree(node);
        return this
    }

    DataModelNavigator div(String node) {
        return rightShift(node)
    }
}
