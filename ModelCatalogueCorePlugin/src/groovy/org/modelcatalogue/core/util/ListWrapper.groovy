package org.modelcatalogue.core.util
/**
 * Wrapper used for easier marshalling of relations result lists
 */
abstract class ListWrapper {
    String next
    String previous
    String itemType
    int total
    int page
    int offset
    List items
}
