package org.modelcatalogue.core.util.lists

/**
 * This is totally for letting the front-end know the total.
 * @param <T>
 */
interface ListWithTotal<T> {

    Long getTotal()
    List<T> getItems()
    /**
     * If this method is called with value total0, then immediately afterwards, getTotal() should return total0.
     * In the simple case of the total being stored in a field, this would just be a setter.
     * @param total
     */
    void totalKnownAlready(Long total)

}
