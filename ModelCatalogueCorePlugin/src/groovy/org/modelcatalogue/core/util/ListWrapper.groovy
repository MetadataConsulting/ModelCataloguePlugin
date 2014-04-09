package org.modelcatalogue.core.util
/**
 * Wrapper used for easier marshalling of relations result lists
 */
abstract class ListWrapper<T> {
    String next
    String previous
    Class<T> itemType
    int total
    int page
    int offset
    List<T> items
}
