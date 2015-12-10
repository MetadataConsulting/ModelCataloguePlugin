package org.modelcatalogue.core

import geb.navigator.Navigator

class NavigatorCondition {

    final AbstractModelCatalogueGebSpec spec
    final Closure<Navigator> navigator

    NavigatorCondition(AbstractModelCatalogueGebSpec spec, Closure<Navigator> navigator) {
        this.spec = spec
        this.navigator = navigator
    }

    boolean isDisplayed() {
        spec.noStale(50, navigator) {
            it.displayed
        }
    }

    boolean isGone() {
        spec.waitFor {
            !navigator().displayed
        }
    }

    boolean is(String text) {
        isDisplayed()
        spec.noStale(50, navigator) {
            it.text().trim() == text
        }
    }

    boolean contains(String text) {
        isDisplayed()
        spec.noStale(50, navigator) {
            it.text().contains text
        }
    }

    boolean asBoolean() {
        isDisplayed()
    }

    @Override
    String toString() {
        Navigator current = navigator()
        return "Navigator condition based on ${current} (text='${current.text().trim()}', size=${current.size()})".toString()
    }
}
