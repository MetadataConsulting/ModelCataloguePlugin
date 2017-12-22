package org.modelcatalogue.core.util.lists

/**
 * This is totally for letting the front-end know the total.
 * @param <T>
 */
interface ListWithTotal<T> {

    Long getTotal()
    List<T> getItems()
    void totalKnownAlready(Long total) // if this is called with value total0, then immediately afterwards, getTotal() should return total0.

}
