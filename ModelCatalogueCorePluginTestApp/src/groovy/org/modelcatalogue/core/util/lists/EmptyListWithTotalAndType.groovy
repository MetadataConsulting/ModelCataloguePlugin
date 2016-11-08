package org.modelcatalogue.core.util.lists

class EmptyListWithTotalAndType<T> implements ListWithTotalAndType<T>{

    Class<T> itemType

    @Override
    Long getTotal() {
        return 0
    }

    @Override
    List<T> getItems() {
        return []
    }
}
