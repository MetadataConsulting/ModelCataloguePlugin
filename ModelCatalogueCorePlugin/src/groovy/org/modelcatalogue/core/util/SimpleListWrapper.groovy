package org.modelcatalogue.core.util

import grails.util.GrailsNameUtils

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class SimpleListWrapper<T> implements ListWrapper<T>{
    String name
    String base
    String next
    String previous
    Class<T> itemType
    String sort
    String order
    Long total
    int page
    int offset
    List<T> items

    @Override
    String getElementName() {
        return name ?: GrailsNameUtils.getPropertyName(getClass().getSimpleName())
    }
}
