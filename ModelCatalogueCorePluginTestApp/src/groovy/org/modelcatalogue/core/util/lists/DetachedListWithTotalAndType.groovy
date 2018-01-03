package org.modelcatalogue.core.util.lists

import grails.gorm.DetachedCriteria

class DetachedListWithTotalAndType<T> extends CustomizableJsonListWithTotalAndType<T> {

    final DetachedCriteria<T> criteria
    final Map<String, Object> params

    private Long total = null
    private List<T> items = null

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        create(params, new DetachedCriteria<T>(type).build(buildClosure))
    }

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, DetachedCriteria<T> criteria){
        new DetachedListWithTotalAndType<T>(criteria, new HashMap(params))
    }

    private DetachedListWithTotalAndType(DetachedCriteria<T> criteria, Map<String, Object> params) {
        Map<String, Object> theParams = new HashMap(params)

        this.criteria   = criteria
        this.params     = Collections.unmodifiableMap(theParams)
    }


    void totalKnownAlready(Long total) {
        this.total = total
        // if this is set, then getTotal() will just return it directly
    }
    @Override
    Long getTotal() {
        if (total == null) {
            return total = criteria.count()
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
            return (items = Collections.unmodifiableList(criteria.list(params)))
        }
        return items
    }

    @Override
    Class<T> getItemType() {
        return (Class<T>) criteria.getPersistentClass()
    }

}
