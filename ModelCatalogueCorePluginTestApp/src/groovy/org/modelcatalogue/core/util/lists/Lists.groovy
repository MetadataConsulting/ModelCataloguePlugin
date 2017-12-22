package org.modelcatalogue.core.util.lists

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.RelationshipDirection

/**
 * Facade class for creating list with count instances with strong focus on late evaluation so they can be used
 * from different thread then they was created.
 */
class Lists {

    /**
     * Wraps a ListWithTotalAndType to ListWrapper using given params, base and name.
     *
     * @param params    url parameters used to determine pagination and sort settings
     * @param base      base url of the list returned
     * @param list      the list with count and type to be wrapped
     * @return given list as list wrapper
     */
    static <T> ListWrapper<T> wrap(Map params, String base, ListWithTotalAndType<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, list)
    }

    /**
     * Wraps a ListWithTotal to ListWrapper using given params, base and name.
     *
     * @param params    url parameters used to determine pagination and sort settings
     * @param type      type to be used as wrapper's itemType
     * @param base      base url of the list returned
     * @param name      name of the list used currently only in XML exports
     * @param list      the list with count to be wrapped
     * @return given list as list wrapper
     */
    static <T> ListWrapper<T> wrap(Map params, Class<T> type, String base, ListWithTotal<T> list){
        if (list instanceof ListWrapper) return list
        ListWithTotalAndTypeWrapper.create(params, base, list instanceof ListWithTotalAndType ? list : new ListWithTotalWrapper<T>(type, list))
    }

    /**
     * Returns ListWithTotalAndType which contains all elements of given type, optionaly paginated by params.
     *
     * This list supports classification.
     *
     * @param params    url parameters used to determine pagination and sort settings
     * @param type      type to be used as wrapper's itemType
     * @return
     */
    static <T> CustomizableJsonListWithTotalAndType<T> all(Map params, Class<T> type){
        fromCriteria(params, type, {})
    }

    /**
     * Returns ListWrapper which contains all elements of given type, optionaly paginated by params.
     *
     * This list supports classification.
     *
     * @param params    url parameters used to determine pagination and sort settings
     * @param type      type to be used as wrapper's itemType
     * @param base      base url of the list returned
     * @param name      name of the list used currently only in XML exports
     * @return
     */
    static <T> ListWrapper<T> all(Map params, Class<T> type, String base){
        fromCriteria(params, type, base, {})
    }


    /**
     * Creates new ListWrapper for given criteria specified by the build closure.
     *
     * This list supports classification.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              base type for the DetachedCriteria
     * @param base              base url of the list returned
     * @param name              name of the list used currently only in XML exports
     * @param buildClosure      DetachedCriteria build closure
     * @return new ListWrapper for given criteria specified by the build closure
     *
     * @see DetachedCriteria
     */
    static <T> ListWrapper<T> fromCriteria(Map params, Class<T> type, String base, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        wrap(params, base, DetachedListWithTotalAndType.create(params, type, buildClosure))
    }

    /**
     * Creates new ListWithTotalAndType for given criteria specified by the build closure.
     *
     * This list supports classification.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param buildClosure      DetachedCriteria build closure
     * @return new ListWithTotalAndType for given criteria specified by the build closure
     *
     * @see DetachedCriteria
     */
    static <T> CustomizableJsonListWithTotalAndType<T> fromCriteria(Map params, Class<T> type, @DelegatesTo(DetachedCriteria) Closure buildClosure){
        DetachedListWithTotalAndType.create(params, new DetachedCriteria<T>(type).build(buildClosure))
    }

    /**
     * Creates new ListWrapper for existing criteria.
     *
     * This list supports classification.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param base              base url of the list returned
     * @param criteria          existing criteria
     * @return new ListWrapper for existing criteria
     *
     * @see DetachedCriteria
     */
    static <T> ListWrapper<T> fromCriteria(Map params, String base, DetachedCriteria<T> criteria){
        wrap(params, base, DetachedListWithTotalAndType.create(params, criteria))
    }


    /**
     * Creates new ListWithTotalAndType for existing criteria.
     *
     * This list supports classification.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param criteria          existing criteria
     * @return new ListWithTotalAndType for existing criteria
     *
     * @see DetachedCriteria
     */
    static <T> CustomizableJsonListWithTotalAndType<T> fromCriteria(Map params, DetachedCriteria<T> criteria){
        DetachedListWithTotalAndType.create(params, criteria)
    }

    /**
     * Creates new ListWithTotalAndType which items will initialized by itemsClosure closure.
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param itemsClosure      closure returning the items of the list
     * @param totalClosure      closure returning the total count of the items
     * @return new ListWithTotalAndType which items will initialized by itemsClosure closure
     */
    static <T> CustomizableJsonListWithTotalAndType<T> lazy(Map params, Class<T> type, Closure<List<T>> itemsClosure, Closure<Long> totalClosure = null){
        LazyListWithTotalAndType.create(params, type, itemsClosure, totalClosure)
    }

    /**
     * Creates new ListWithTotalAndType whose items will initialized by listMethods.
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param itemsClosure      closure returning the items of the list
     * @param totalClosure      closure returning the total count of the items
     * @return new ListWithTotalAndType which items will initialized by itemsClosure closure
     */
    static <T> CustomizableJsonListWithTotalAndType<T> methodNotClosureLazy(Map params, Class<T> type, MethodNotClosureLazyListWithTotalAndType.ListMethods<T> listMethods){
        MethodNotClosureLazyListWithTotalAndType.create(params, type, listMethods)
    }



    /**
     * Creates new ListWrapper for lazily evaluated list.
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param base              base url of the list returned
     * @param name              name of the root element
     * @param itemsClosure      closure returning the items of the list
     * @param totalClosure      closure returning the total count of the items
     * @return new ListWrapper for lazy evaluated list
     *
     * @see DetachedCriteria
     */
    static <T> ListWrapper<T> lazy(Map params, Class<T> type, String base, Closure<List<T>> itemsClosure, Closure<Long> totalClosure = null){
        wrap(params, base, LazyListWithTotalAndType.create(params, type, itemsClosure, totalClosure))
    }


    /**
     * Creates new ListWithTotalAndType from HQL query.
     *
     * The query has to start with keyword 'from' followed by simple name of the entity and
     * alias which is the first lower-cased letter of the entity name e.g. 'from Entity e'.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param query             HQL query
     * @param arguments         named arguments for the HQL query
     * @return new ListWithTotalAndType from HQL query
     */
    static <T> CustomizableJsonListWithTotalAndType<T> fromQuery(Map params, Class<T> type, String query, Map<String, Object> arguments = [:]){
        QueryListWithTotalAndType.create(params, type, query, arguments)
    }

    /**
     * Creates new ListWithTotalAndType from HQL query.
     *
     * The query has to start with keyword 'from' followed by simple name of the entity and
     * alias which is the first lower-cased letter of the entity name e.g. 'from Entity e'.
     *
     * @param params            url parameters used to determine pagination and sort settings
     * @param type              type to be used as wrapper's itemType
     * @param listQuery         HQL query to be used to select the items
     * @param countQuery        HQL query to be used to determine the total count of the items
     * @param arguments         named arguments for the HQL query
     * @return new ListWithTotalAndType from HQL query
     */
    static <T> CustomizableJsonListWithTotalAndType<T> fromQuery(Map params, Class<T> type, String listQuery, String countQuery, Map<String, Object> arguments = [:]){
        QueryListWithTotalAndType.create(params, type, listQuery, countQuery, arguments)
    }
    //<-- not providing wrapper variant because it should be used in service classes

    static <T> ListWithTotalAndType<T> emptyListWithTotalAndType(Class<T> type) {
        new EmptyListWithTotalAndType<T>(itemType: type)
    }

    static Map<String, String> nextAndPreviousLinks(Map<String, Object> params, String baseLink, Long total) {
        def link = baseLink.contains('?') ? "${baseLink}&" : "${baseLink}?"
        if (params.max) {
            link += "max=${params.max ?: 10}"
        }
        params.each { String k, Object v ->
            if (k == 'sort' && RelationshipDirection.values()*.sortProperty.contains(v)) {
                return
            }
            if (v && !(k in ['offset', 'max', 'type', 'action', 'controller', 'id']) && !(baseLink =~ /[\?&]${k}=/)) {
                link += "&$k=$v"
            }
        }

        def nextLink = ""
        def previousLink = ""
        int totalInt = total.toInteger()
        int max = params.max ? params.max as int : 0
        if (max && max < totalInt) {
            int offset = params.offset ? params.offset as int : 0
            int prev = offset - max
            int next = offset + max
            if (next < totalInt) {
                nextLink = "${link}&offset=${next}"
            }
            if (prev >= 0) {
                previousLink = "${link}&offset=${prev}"
            }
        }
        [
                next: nextLink,
                previous: previousLink
        ]
    }
}
