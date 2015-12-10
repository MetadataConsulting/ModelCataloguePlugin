package org.modelcatalogue.core

class FormFiller {

    final AbstractModelCatalogueGebSpec spec;
    final String nameOrId

    FormFiller(AbstractModelCatalogueGebSpec spec, String nameOrId) {
        this.spec = spec
        this.nameOrId = nameOrId
    }

    void with(Object value) {
        spec.noStale({spec.$("input[id=$nameOrId], #$nameOrId")}) {
            it.value(value)
        }
    }
}
