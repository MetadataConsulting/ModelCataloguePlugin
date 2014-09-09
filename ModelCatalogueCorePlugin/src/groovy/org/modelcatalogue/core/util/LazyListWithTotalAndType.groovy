package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

/**
 * Created by ladin on 14.07.14.
 */
class LazyListWithTotalAndType<T> implements ListWithTotalAndType<T> {

    final Class<T> itemType
    final Map<String, Object> params

    final Closure<List<T>> itemsClosure
    final Closure<Long> totalClosure

    private Long total = null
    private List<T> items = null

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, Closure itemsClosure){
        new LazyListWithTotalAndType<T>(params, type, itemsClosure, null)
    }

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, Closure<List<T>> itemsClosure, Closure<Long> totalClosure){
        new LazyListWithTotalAndType<T>(params, type, itemsClosure, totalClosure)
    }

    private LazyListWithTotalAndType(Map<String, Object> params, Class<T> type, Closure<List<T>> itemsClosure, Closure<Long> totalClosure) {
        Map<String, Object> theParams = new HashMap(params)

        this.params = Collections.unmodifiableMap(theParams)
        this.itemType = type
        this.itemsClosure = itemsClosure
        this.totalClosure = totalClosure
    }

    @Override
    Long getTotal() {
        if (total == null) {
            return total = totalClosure ? totalClosure() : getItems().size()
        }
        return total
    }

    static void setTotal(Long ignored) {
        throw new UnsupportedOperationException("Setting total is not supported")
    }

    static void setItems(List<T> ignored) {
        throw new UnsupportedOperationException("Setting items is not supported")
    }

    @Override
    List<T> getItems() {
        if (items == null)  {
            return (items = Collections.unmodifiableList(itemsClosure(params)))
        }
        return items
    }
}
