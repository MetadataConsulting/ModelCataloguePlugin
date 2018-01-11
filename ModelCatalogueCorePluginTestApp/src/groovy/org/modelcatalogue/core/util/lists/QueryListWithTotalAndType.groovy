package org.modelcatalogue.core.util.lists

class QueryListWithTotalAndType<T> extends CustomizableJsonListWithTotalAndType<T> {

    final String listQuery
    final String countQuery
    final Class<T> type
    final Map<String, Object> params
    final Map<String, Object> arguments

    private Long total = null
    private List<T> items = null

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, String query){
        create(params, type, query, [:])
    }

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, String query, Map<String, Object> arguments){
        def alias = type.simpleName[0].toLowerCase()
        String expectedStart = "from ${type.simpleName} ${alias}"
        if (!query.trim().startsWith(expectedStart)) {
            throw new IllegalArgumentException("Query must start with '$expectedStart' but was ${query.trim()}")
        }
        create(params, type, "select $alias $query", "select count($alias) $query", arguments)
    }

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, String listQuery, String countQuery){
        create(params, type, listQuery, countQuery, [:])
    }

    public static <T> CustomizableJsonListWithTotalAndType<T> create(Map params, Class<T> type, String listQuery, String countQuery, Map<String, Object> arguments){
        if (!countQuery.trim().toLowerCase().startsWith("select count(")) {
            throw new IllegalArgumentException("Query must start with 'select count(<alias>)' but was ${countQuery.trim()}")
        }
        if (params.sort && listQuery && !listQuery.contains('order by')) {
            def alias = type.simpleName[0].toLowerCase()
            String expectedStart = "select ${alias} from ${type.simpleName} ${alias}"
            if (!listQuery.trim().replaceAll(/\s+/,' ').startsWith(expectedStart)) {
                throw new IllegalArgumentException("If you want to use sort and order parameters the query must start with '$expectedStart' but was '${listQuery.trim()}'")
            }
            if (params.order == 'desc') {
                listQuery = "${listQuery} order by ${alias}.${params.sort} desc"
            } else {
                listQuery = "${listQuery} order by ${alias}.${params.sort}"
            }
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


    void totalKnownAlready(Long total) {
        this.total = total
        // if this is set, then getTotal() will just return it directly
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

    @Override
    String toString() {
        return """
        QueryListWithTotalAndType:
            count query: $countQuery
            list query: $listQuery
        """.stripIndent().trim()
    }
}
