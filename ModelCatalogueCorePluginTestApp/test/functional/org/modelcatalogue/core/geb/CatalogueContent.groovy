package org.modelcatalogue.core.geb

import geb.navigator.Navigator

class CatalogueContent {

    final CatalogueContent parent
    final String selector
    final Map<String, Object> args

    static CatalogueContent create(Map arguments) {
        new CatalogueContent(null, "*", arguments)
    }

    static CatalogueContent create(String selector) {
        new CatalogueContent(null, selector, [:])
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector) {
        new CatalogueContent(null, selector, arguments)
    }

    CatalogueContent(CatalogueContent parent, String selector, Map<String, Object> args) {
        this.parent = parent
        this.selector = selector
        this.args = args
    }

    Navigator select(AbstractModelCatalogueGebSpec spec) {
        if (parent) {
            return parent.select(spec).find(args, selector)
        }
        return spec.$(args, selector)
    }

    CatalogueContent find(Map arguments = [:], String selector) {
        return new CatalogueContent(this, selector, arguments)
    }
}
