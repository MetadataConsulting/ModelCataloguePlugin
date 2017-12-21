package org.modelcatalogue.core.util.lists

class ListWithTotalAndTypeImpl<T> implements ListWithTotalAndType<T> {

    Class<T> itemType
    Long total
    List<T> items

    ListWithTotalAndTypeImpl(Class<T> itemType, List<T> items, Long total) {
        this.itemType = itemType
        this.total = total
        this.items = items
    }
}
