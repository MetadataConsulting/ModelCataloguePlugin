package org.modelcatalogue.core.view

import groovy.transform.CompileStatic

@CompileStatic
class PaginationViewModelImpl implements PaginationViewModel {
    int total
    int offset
    int max
}
