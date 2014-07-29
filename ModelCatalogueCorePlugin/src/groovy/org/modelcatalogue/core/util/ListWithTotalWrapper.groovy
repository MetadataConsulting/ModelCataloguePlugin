package org.modelcatalogue.core.util
/**
 * Created by ladin on 14.07.14.
 */
class ListWithTotalWrapper<T> implements ListWithTotalAndType<T> {

    final Class<T> itemType
    @Delegate private final ListWithTotal<T> list

    ListWithTotalWrapper(Class<T> itemType, ListWithTotal<T> list) {
        this.itemType = itemType
        this.list = list
    }

}
