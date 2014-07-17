package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

/**
 * Created by ladin on 14.07.14.
 */
class DetachedListWithTotalAndType<T> implements ListWithTotalAndType<T> {

    final DetachedCriteria<T> criteria
    final Map<String, Object> params

    private Long total = null
    private List<T> items = null

    public static <T> DetachedListWithTotalAndType<T> create(Map params, Class<T> type, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        create(params, new DetachedCriteria<T>(type).build(buildClosure))
    }

    public static <T> DetachedListWithTotalAndType<T> create(Map params, DetachedCriteria<T> criteria){
        new DetachedListWithTotalAndType<T>(criteria, new HashMap(params))
    }

    private DetachedListWithTotalAndType(DetachedCriteria<T> criteria, Map<String, Object> params) {
        Map<String, Object> theParams = new HashMap(params)

        this.criteria   = criteria
        this.params     = Collections.unmodifiableMap(theParams)
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
            return (items = criteria.list(params))
        }
        return Collections.unmodifiableList(items)
    }

    @Override
    Class<T> getItemType() {
        return (Class<T>) criteria.getPersistentClass()
    }

}
