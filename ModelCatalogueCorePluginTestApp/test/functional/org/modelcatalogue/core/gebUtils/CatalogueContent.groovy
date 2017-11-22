package org.modelcatalogue.core.gebUtils

import geb.navigator.Navigator

class CatalogueContent {

    final CatalogueContent parent
    final String selector
    final Map<String, Object> args
    final Closure beforeSelect

    static CatalogueContent create(Map arguments) {
        new CatalogueContent(null, "*", arguments, null)
    }

    static CatalogueContent create(String selector) {
        new CatalogueContent(null, selector, [:], null)
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector) {
        new CatalogueContent(null, selector, arguments, null)
    }

    static CatalogueContent create(Map arguments, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure beforeSelect) {
        new CatalogueContent(null, "*", arguments, beforeSelect)
    }

    static CatalogueContent create(String selector, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure beforeSelect) {
        new CatalogueContent(null, selector, [:], beforeSelect)
    }

    static CatalogueContent create(Map<String, Object> arguments, String selector, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure beforeSelect) {
        new CatalogueContent(null, selector, arguments, beforeSelect)

    }

    CatalogueContent(CatalogueContent parent, String selector, Map<String, Object> args, @DelegatesTo(AbstractModelCatalogueGebSpec) Closure beforeSelect) {
        this.parent = parent
        this.selector = selector
        this.args = args
        this.beforeSelect = beforeSelect
    }

    Navigator select(AbstractModelCatalogueGebSpec spec) {
        if (parent) {
            return parent.select(spec).find(args, selector)
        }
        return spec.$(args, selector)
    }

    CatalogueContent find(Map arguments = [:], String selector) {
        return new CatalogueContent(this, selector, arguments, beforeSelect)
    }


    @Override
    public String toString() {
        return "CatalogueContent{" +
            "parent=" + parent +
            ", selector='" + selector + '\'' +
            ", args=" + args +
            '}';
    }
}
