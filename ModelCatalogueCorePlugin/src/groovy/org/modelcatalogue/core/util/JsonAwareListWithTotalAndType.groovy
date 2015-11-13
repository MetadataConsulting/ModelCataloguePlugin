package org.modelcatalogue.core.util

interface JsonAwareListWithTotalAndType<T> extends ListWithTotalAndType<T> {
    List<Map<String, Object>> getJsonItems()
}
