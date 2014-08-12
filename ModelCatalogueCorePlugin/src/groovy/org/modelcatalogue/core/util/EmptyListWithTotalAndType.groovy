package org.modelcatalogue.core.util

/**
 * Created by ladin on 05.08.14.
 */
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
