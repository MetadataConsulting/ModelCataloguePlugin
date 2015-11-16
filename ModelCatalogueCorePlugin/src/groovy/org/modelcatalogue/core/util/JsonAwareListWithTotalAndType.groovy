package org.modelcatalogue.core.util

interface JsonAwareListWithTotalAndType<T> extends ListWithTotalAndType<T> {
    List<Object> getJsonItems()
}
