package org.modelcatalogue.core.util.lists

class ListWithTotalWrapper<T> implements ListWithTotalAndType<T> {

    final Class<T> itemType
    @Delegate private final ListWithTotal<T> list

    ListWithTotalWrapper(Class<T> itemType, ListWithTotal<T> list) {
        this.itemType = itemType
        this.list = list
    }

}
