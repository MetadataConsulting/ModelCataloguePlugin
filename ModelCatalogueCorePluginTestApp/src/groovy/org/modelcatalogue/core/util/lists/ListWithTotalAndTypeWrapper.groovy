package org.modelcatalogue.core.util.lists

class ListWithTotalAndTypeWrapper<T> implements ListWrapper<T> {

    final Map<String, Object> params

    final String base
    final String sort

    final String order
    final int page
    final int offset

    String next
    String previous

    String timeTaken = ''

    @Delegate ListWithTotalAndType<T> list = null

    /**
     * Shouldn't have to do this because there's @Delegate but...
     * @param total
     */
    void totalKnownAlready(Long total) {
        list.totalKnownAlready(total)
    }

    public static <T> ListWrapper<T> create(Map params, String base, ListWithTotalAndType<T> list){
        new ListWithTotalAndTypeWrapper<T>(list, base, params)
    }

    public static <T> ListWrapper<T> createWithTime(Map params, String base, ListWithTotalAndType<T> list, String timeTaken){
        ListWrapper<T> result = new ListWithTotalAndTypeWrapper<T>(list, base, params)
        result.timeTaken = timeTaken
        return result

    }
    private ListWithTotalAndTypeWrapper(ListWithTotalAndType<T> list, String base, Map<String, Object> params) {
        this.list = list

        this.base = base
        this.params = Collections.unmodifiableMap(new HashMap(params))

        this.sort = params.sort
        this.order = params.order

        this.page = params.max ? Integer.valueOf(params.max.toString()) : 10
        this.offset = params.offset ? Integer.valueOf(params.offset.toString()) : 0
    }


    String getNext() {
        if (next != null) {
            return next
        }
        Map<String, String> links = Lists.nextAndPreviousLinks(params, getBase(), getTotal())
        this.previous = links.previous
        this.next = links.next
    }

    static void setNext(String ignored) {
        throw new UnsupportedOperationException("Setting next link from outside the wrapper is not supported")
    }

    static void setPrevious(String ignored) {
        throw new UnsupportedOperationException("Setting previous link from outside the wrapper is not supported")
    }

    String getPrevious() {
        if (previous != null) {
            return previous
        }
        Map<String, String> links = Lists.nextAndPreviousLinks(params, getBase(), getTotal())
        this.next = links.next
        this.previous = links.previous
    }

}
