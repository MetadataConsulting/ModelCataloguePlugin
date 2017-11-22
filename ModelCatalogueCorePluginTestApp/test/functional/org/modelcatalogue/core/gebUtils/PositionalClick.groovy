package org.modelcatalogue.core.gebUtils

class PositionalClick {

    private boolean first
    private AbstractModelCatalogueGebSpec spec

    PositionalClick(boolean first, AbstractModelCatalogueGebSpec spec) {
        this.first = first
        this.spec = spec
    }

    void of(CatalogueAction action) {
        if (first) {
            spec.click action.first()
        } else {
            spec.click action.last()
        }
    }
}
