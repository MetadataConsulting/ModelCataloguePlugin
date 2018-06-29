package org.modelcatalogue.core.dataimport.excel

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement

@CompileStatic
class Excel {
    final static int MAX_CELL_LENGTH = 32767

    static String truncateForCell(String s) {
        return s && s.length() > MAX_CELL_LENGTH ? s.substring(0, MAX_CELL_LENGTH) : s
    }

    static String getDescription(CatalogueElement ce, String defaultValue = '') {
        return truncateForCell(ce?.description ?: defaultValue)
    }
}
