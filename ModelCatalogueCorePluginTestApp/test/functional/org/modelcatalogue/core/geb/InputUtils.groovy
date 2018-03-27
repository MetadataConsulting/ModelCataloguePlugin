package org.modelcatalogue.core.geb

trait InputUtils {
    void fillInput(def input, String value) {
        for ( char c : value.toCharArray() ) {
            input << "${c}".toString()
        }
    }
}
