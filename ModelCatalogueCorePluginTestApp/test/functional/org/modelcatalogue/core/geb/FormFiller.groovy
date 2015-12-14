package org.modelcatalogue.core.geb

import geb.navigator.Navigator

class FormFiller {

    final AbstractModelCatalogueGebSpec spec;
    final Closure<Navigator> navigator

    FormFiller(AbstractModelCatalogueGebSpec spec, Closure<Navigator> navigator) {
        this.spec = spec
        this.navigator = (Closure<Navigator>) navigator.clone()

        this.navigator.delegate = spec
        this.navigator.resolveStrategy = Closure.DELEGATE_FIRST
    }

    void with(Object value) {
        spec.noStale(navigator) {
            it.value(value)
        }
    }
}
