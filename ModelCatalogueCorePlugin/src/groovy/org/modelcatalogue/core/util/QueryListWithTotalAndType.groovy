package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria

/**
 * Created by ladin on 14.07.14.
 */
class QueryListWithTotalAndType<T> implements ListWithTotalAndType<T> {

    final String listQuery
    final String countQuery
    final Class<T> type
    final Map<String, Object> params
    final Map<String, Object> arguments

    private Long total = null
    private List<T> items = null

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, String query){
        create(params, type, query, [:])
    }

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, String query, Map<String, Object> arguments){
        def alias = type.simpleName[0].toLowerCase()
        String expectedStart = "from ${type.simpleName} ${alias}"
        if (!query.trim().startsWith(expectedStart)) {
            throw new IllegalArgumentException("Query must start with '$expectedStart' but was ${query.trim()}")
        }
        new QueryListWithTotalAndType<T>(query, "select count($alias) $query", type, new HashMap(params), arguments)
    }

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, String listQuery, String countQuery){
        create(params, type, listQuery, countQuery, [:])
    }

    public static <T> ListWithTotalAndType<T> create(Map params, Class<T> type, String listQuery, String countQuery, Map<String, Object> arguments){
        if (!countQuery.trim().toLowerCase().startsWith("select count(")) {
            throw new IllegalArgumentException("Query must start with 'select count(<alias>)' but was ${countQuery.trim()}")
        }
        new QueryListWithTotalAndType<T>(listQuery, countQuery, type, new HashMap(params), arguments)
    }

    private QueryListWithTotalAndType(String listQuery, String countQuery, Class<T> type, Map<String, Object> params, Map<String, Object> arguments) {
        this.listQuery = listQuery
        this.countQuery = countQuery
        this.type = type
        this.params = Collections.unmodifiableMap(new HashMap(params))
        this.arguments = Collections.unmodifiableMap(new HashMap(arguments))
    }

    @Override
    Long getTotal() {
        if (total == null) {
            return total = type.executeQuery(countQuery, arguments)[0]
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
            return (items = Collections.unmodifiableList(type.executeQuery(listQuery, arguments, params)))
        }
        return items
    }

    @Override
    Class<T> getItemType() {
        return type
    }

}
