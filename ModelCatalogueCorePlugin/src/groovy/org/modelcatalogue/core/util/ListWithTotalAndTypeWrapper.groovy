package org.modelcatalogue.core.util
/**
 * Created by ladin on 14.07.14.
 */
class ListWithTotalAndTypeWrapper<T> implements ListWrapper<T> {

    final Map<String, Object> params

    final String base
    final String next
    final String previous
    final String elementName

    final String sort
    final String order
    final int page
    final int offset

    @Delegate private ListWithTotalAndType<T> list = null

    public static <T> ListWrapper<T> create(Map params, String base, ListWithTotalAndType<T> list){
        create(params, base, null, list)
    }

    public static <T> ListWrapper<T> create(Map params, String base, String name, ListWithTotalAndType<T> list){
        new ListWithTotalAndTypeWrapper<T>(list, base, name, params)
    }

    private ListWithTotalAndTypeWrapper(ListWithTotalAndType<T> list, String base, String name, Map<String, Object> params) {
        Map<String, Object> theParams = new HashMap(params)

        this.list = list

        this.base = base
        this.elementName = name
        this.params = Collections.unmodifiableMap(theParams)

        Map<String, String> links = Lists.nextAndPreviousLinks(params, getBase(), getTotal())
        this.next = links.next
        this.previous = links.previous

        this.sort = params.sort
        this.order = params.order

        this.page = params.max ? Integer.valueOf(params.max.toString()) : 10
        this.offset = params.offset ? Integer.valueOf(params.offset.toString()) : 0
    }

}
