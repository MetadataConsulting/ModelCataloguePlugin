package org.modelcatalogue.core.util.lists

class EmptyListWithTotalAndType<T> implements ListWithTotalAndType<T>{

    Class<T> itemType
    Long total = 0

    @Override
    Long getTotal() {
        return total
    }

    @Override
    List<T> getItems() {
        return []
    }


    @Override
    void totalKnownAlready(Long total) {
        this.total = total
        // if this is set, then getTotal() will just return it directly
    }
}
