package org.modelcatalogue.core.util.lists

/**
 * Based on LazyListWithTotalAndType but using an inner class rather than closures. Classic Java
 * @param <T>
 */
class MethodNotClosureLazyListWithTotalAndType<T> extends CustomizableJsonListWithTotalAndType<T> {

    final Class<T> itemType
    final Map<String, Object> params
    interface ListMethods<T> {
        List<T> getItems(Map params)
        Long getTotal()

    }
    final ListMethods<T> listMethods

    private Long total = null
    private List<T> items = null


    static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, ListMethods listMethods){
        new MethodNotClosureLazyListWithTotalAndType<T>(params, type, listMethods)
    }

    private MethodNotClosureLazyListWithTotalAndType(Map<String, Object> params, Class<T> type, ListMethods listMethods) {
        Map<String, Object> theParams = new HashMap(params)

        this.params = Collections.unmodifiableMap(theParams)
        this.itemType = type
        this.listMethods = listMethods
    }

    @Override
    void totalKnownAlready(Long total) {
        this.total = total
        // if this is set, then getTotal() will just return it directly
    }

    @Override
    Long getTotal() {
        if (total == null) {
            return total = listMethods.getTotal()
            // this could query EVERY DATA ELEMENT IN A MODEL because no params with max are passed in.
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
            return (items = Collections.unmodifiableList(listMethods.getItems(params)))
        }
        return items
    }
}
