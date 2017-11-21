package org.modelcatalogue.core.gebUtils

import geb.navigator.Navigator
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class NavigatorCondition {

    private static final int NUM_OF_RETRIES = 6

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
            it.first().displayed
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
            it.first().hasClass(clazz)
        }
    }

    /**
     * @return return true if the element was either not present on the websited or is present but diappears
     */
    boolean isGone() {
        spec.noStale(NUM_OF_RETRIES, true, navigator) { !it.first().displayed }
    }

    /**
     * @return true if the element is not displayed on the website
     */
    boolean isMissing() {
        spec.noStale(NUM_OF_RETRIES, "gone", navigator) { it.first().displayed ? "present" : "gone" } == "gone"
    }

    boolean is(String text) {
        isDisplayed()
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.first().text().trim() == text
        }
    }

    boolean contains(String text) {
        isDisplayed()
        spec.noStale(NUM_OF_RETRIES, navigator) {
            it.first().text().contains text
        }
    }

    boolean missing(String text) {
        isDisplayed()
        spec.noStale(NUM_OF_RETRIES, navigator) {
            !it.first().text().contains(text)
        }
    }

    SizeCondition present(int times) {
        return new SizeCondition(times, this)
    }

    boolean present(Keywords once) {
        if (once == Keywords.ONCE) {
            return present(1).times
        }
        throw new IllegalArgumentException("Only 'once' allowed here")
    }

    boolean test(int retries = 1, @ClosureParams(value=FromString, options='geb.navigator.Navigator') Closure<Boolean> test) {
        spec.noStale(retries, navigator, test)
    }

    boolean asBoolean() {
        isDisplayed()
    }

    @Override
    String toString() {
        Navigator current = navigator()
        return "Navigator condition based on ${current} (text='${current.first().text()?.trim()}', size=${current.size()})".toString()
    }
}
