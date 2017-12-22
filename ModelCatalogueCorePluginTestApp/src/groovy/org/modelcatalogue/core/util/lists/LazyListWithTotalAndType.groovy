package org.modelcatalogue.core.util.lists

class LazyListWithTotalAndType<T> extends CustomizableJsonListWithTotalAndType<T> {

    final Class<T> itemType
    final Map<String, Object> params

    final Closure<List<T>> itemsClosure
    final Closure<Long> totalClosure

    private Long total = null
    private List<T> items = null

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, Closure itemsClosure){

        new LazyListWithTotalAndType<T>(params, type, itemsClosure, null)
    }

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, Closure<List<T>> itemsClosure, Closure<Long> totalClosure){
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
    void totalKnownAlready(Long total) {
        this.total = total
        // if this is set, then getTotal() will just return it directly
    }

    @Override
    Long getTotal() {
        if (total == null) {
            return total = totalClosure ? totalClosure() : getItems().size()
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
            return (items = Collections.unmodifiableList(itemsClosure(params)))
        }
        return items
    }
}
