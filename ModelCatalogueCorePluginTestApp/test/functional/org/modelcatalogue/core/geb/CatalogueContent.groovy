package org.modelcatalogue.core.geb

import geb.navigator.Navigator
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class CatalogueContent {

    final CatalogueContent parent
    final String selector
    final Map<String, Object> args
    final Closure beforeRetry

    static CatalogueContent create(Map arguments) {
        new CatalogueContent(null, "*", arguments, null)
    }

    static CatalogueContent create(String selector) {
        new CatalogueContent(null, selector, [:], null)
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector) {
        new CatalogueContent(null, selector, arguments, null)
    }

    static CatalogueContent create(Map arguments, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure retryBody) {
        new CatalogueContent(null, "*", arguments, retryBody)
    }

    static CatalogueContent create(String selector, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure retryBody) {
        new CatalogueContent(null, selector, [:], retryBody)
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure retryBody) {
        new CatalogueContent(null, selector, arguments, retryBody)

    }

    CatalogueContent(CatalogueContent parent, String selector, Map<String, Object> args, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure retryBody) {
        this.parent = parent
        this.selector = selector
        this.args = args
        this.beforeRetry = retryBody
    }

    Navigator select(AbstractModelCatalogueGebSpec spec) {
        if (parent) {
            return parent.select(spec).find(args, selector)
        }
        return spec.$(args, selector)
    }

    CatalogueContent find(Map arguments = [:], String selector) {
        return new CatalogueContent(this, selector, arguments, beforeRetry)
    }
}
