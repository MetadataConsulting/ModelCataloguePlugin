package org.modelcatalogue.core.geb

import geb.navigator.Navigator

class CatalogueContent {

    final String selector
    final Map<String, Object> args

    static CatalogueContent create(Map arguments) {
        new CatalogueContent("*", arguments)
    }

    static CatalogueContent create(String selector) {
        new CatalogueContent(selector, [:])
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector) {
        new CatalogueContent(selector, arguments)
    }

    CatalogueContent(String selector, Map<String, Object> args) {
        this.selector = selector
        this.args = args
    }

    Navigator select(AbstractModelCatalogueGebSpec spec) {
        spec.$(args, selector)
    }
}
