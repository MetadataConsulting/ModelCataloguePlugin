package org.modelcatalogue.core.geb

import geb.navigator.Navigator
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class NavigatorCondition {

    private static final int NUM_OF_RETRIES = 5

    final AbstractModelCatalogueGebSpec spec
    final Closure<Navigator> navigator

    NavigatorCondition(AbstractModelCatalogueGebSpec spec, Closure<Navigator> navigator) {
        this.spec = spec
        this.navigator = (Closure<Navigator>) navigator.clone()

        this.navigator.delegate = spec
        this.navigator.resolveStrategy = Closure.DELEGATE_FIRST
    }

    boolean isDisplayed() {
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.displayed
        }
    }

    boolean isDisabled() {
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.disabled
        }
    }

    boolean isEnabled() {
        spec.noStale(NUM_OF_RETRIES, navigator) {
            !it.disabled
        }
    }

    boolean has(String clazz) {
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.hasClass(clazz)
        }
    }



    boolean isGone() {
        spec.waitFor {
            !navigator().displayed
        }
    }

    boolean is(String text) {
        isDisplayed()
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.text().trim() == text
        }
    }

    boolean contains(String text) {
        isDisplayed()
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.text().contains text
        }
    }

    SizeCondition present(int times) {
        return new SizeCondition(times, this)
    }

    boolean present(Keywords once) {
        if (once == Keywords.ONCE) {
            return spec.noStale(NUM_OF_RETRIES, true, navigator) { it.size() == 1}
        }
        throw new IllegalArgumentException("Only 'once' allowed here")
    }

    boolean test(int retries = 1, @ClosureParams(value=FromString, options='geb.navigator.Navigator') Closure<Boolean> test) {
        spec.noStale(retries, true, navigator, test)
    }

    boolean asBoolean() {
        isDisplayed()
    }

    @Override
    String toString() {
        Navigator current = navigator()
        return "Navigator condition based on ${current} (text='${current.text()?.trim()}', size=${current.size()})".toString()
    }
}
