package org.modelcatalogue.core.util

interface ListWithTotal<T> {

    Long getTotal()
    List<T> getItems()

}
