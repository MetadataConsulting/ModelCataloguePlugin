package org.modelcatalogue.core.view

import groovy.transform.CompileStatic

@CompileStatic
interface PaginationViewModel {
    int getTotal()
    int getMax()
    int getOffset()
}