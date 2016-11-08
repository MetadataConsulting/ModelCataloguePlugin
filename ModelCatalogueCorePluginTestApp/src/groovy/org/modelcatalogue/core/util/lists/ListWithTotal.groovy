package org.modelcatalogue.core.util.lists

interface ListWithTotal<T> {

    Long getTotal()
    List<T> getItems()

}
