package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

/**
 * Created by ladin on 14.07.14.
 */
class DetachedListWrapper<T> implements ListWrapper<T> {

    final DetachedCriteria<T> criteria
    final Map<String, Object> params
    final String base
    final String next
    final String previous
    final String name

    private Long total = null
    private List<T> items = null

    public static <T> DetachedListWrapper<T> create(Map params, Class<T> type, String base){
        create(params, type, base, (String) null)
    }

    public static <T> DetachedListWrapper<T> create(Map params, Class<T> type, String base, String name){
        create(params, type, base, name, {})
    }

    public static <T> DetachedListWrapper<T> create(Map params, Class<T> type, String base, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        create(params, type, base, null, buildClosure)
    }

    public static <T> DetachedListWrapper<T> create(Map params, Class<T> type, String base, String name, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        create(params, base, name, new DetachedCriteria<T>(type).build(buildClosure))

    }

    public static <T> DetachedListWrapper<T> create(Map params, String base, DetachedCriteria<T> criteria){
        create(params, base, null, criteria)
    }

    public static <T> DetachedListWrapper<T> create(Map params, String base, String name, DetachedCriteria<T> criteria){
        new DetachedListWrapper<T>(criteria, base, name, new HashMap(params))

    }

    private DetachedListWrapper(DetachedCriteria<T> criteria, String base, String name, Map<String, Object> params) {
        Map<String, Object> theParams = new HashMap(params)

        this.criteria   = criteria
        this.base       = base
        this.name       = name
        this.params     = Collections.unmodifiableMap(theParams)

        Map<String, String> links   = SimpleListWrapper.nextAndPreviousLinks(params, getBase(), getTotal())
        this.next                   = links.next
        this.previous               = links.previous

    }

    @Override
    Class<T> getItemType() {
        return (Class<T>) criteria.getPersistentClass()
    }

    @Override
    String getSort() {
        return params.sort
    }

    @Override
    String getOrder() {
        return params.order
    }

    @Override
    int getTotal() {
        if (total == null) {
            return total = criteria.count()
        }
        return total
    }

    void setTotal(int ignored) {
        throw new UnsupportedOperationException("Setting total is not supported")
    }

    void setItems(List<T> ignored) {
        throw new UnsupportedOperationException("Setting items is not supported")
    }

    @Override
    int getPage() {
        return params.max ? Integer.valueOf(params.max.toString()) : 10
    }

    @Override
    int getOffset() {
        return params.offset ? Integer.valueOf(params.offset.toString()) : 0
    }

    @Override
    List<T> getItems() {
        if (items == null)  {
            return (items = criteria.list(params))
        }
        return Collections.unmodifiableList(items)
    }

    @Override
    String getElementName() {
        return name
    }

}
