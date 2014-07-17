package org.modelcatalogue.core.util
/**
 * Created by ladin on 23.04.14.
 */
class ListAndCount<T> implements ListWithTotal<T> {
    List<T> list
    Long count


    List<T> getItems() { list }
    Long getTotal() { count }

}
