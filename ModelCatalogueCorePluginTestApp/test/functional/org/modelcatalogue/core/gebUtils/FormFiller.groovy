package org.modelcatalogue.core.gebUtils

import geb.navigator.Navigator

class FormFiller {

    final AbstractModelCatalogueGebSpec spec;
    final Closure<Navigator> navigator

    private boolean strict = false

    FormFiller(AbstractModelCatalogueGebSpec spec, Closure<Navigator> navigator) {
        this.spec = spec
        this.navigator = (Closure<Navigator>) navigator.clone()

        this.navigator.delegate = spec
        this.navigator.resolveStrategy = Closure.DELEGATE_FIRST
    }

    FormFiller with(Object value) {
        spec.noStale(navigator) {
            it.value(value)
        }
        this
    }

    FormFiller and(Keywords pick) {
        if (pick == Keywords.SELECT) {
            strict = true
        }
        this
    }

    FormFiller first(Keywords item) {
        if (spec.selectCepItemIfExists()) {
            spec.check '.cep-item:not(.show-more-cep-item),.cep-item:not(.create-new-cep-item), .item-found' gone
            return this
        }
        if (!strict || item == Keywords.EXISTING) {
            return this
        }
        throw new IllegalStateException("There is no item to be selected")
    }


    FormFiller getItem() {
        // syntactic sugger
        return this
    }


}
