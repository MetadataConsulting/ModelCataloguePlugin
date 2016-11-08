package org.modelcatalogue.core.util.lists

interface JsonAwareListWithTotalAndType<T> extends ListWithTotalAndType<T> {
    List<Object> getJsonItems()
}
