package org.modelcatalogue.core.util
/**
 * @deprecated it doesn't play well with late evaluation (export as asset).
 */
@Deprecated
class ListAndCount<T> implements ListWithTotal<T> {
    List<T> list
    Long count


    List<T> getItems() { list }
    Long getTotal() { count }

}
