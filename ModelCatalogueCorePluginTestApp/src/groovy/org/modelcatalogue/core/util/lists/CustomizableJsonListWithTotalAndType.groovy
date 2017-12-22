package org.modelcatalogue.core.util.lists

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

/**
 * ListWithTotalAndTypeWrapperMarshaller will ask for jsonItems instead of items for this class, which uses customizer on its own items method.
 * @param <T>
 */
abstract class CustomizableJsonListWithTotalAndType<T> implements JsonAwareListWithTotalAndType<T> {

    private Closure<List<Object>> customizer

    @Override
    List<Object> getJsonItems() {
        if (customizer) {
            return customizer(items)
        }
        return items
    }

    CustomizableJsonListWithTotalAndType<T> customize(@ClosureParams(value=FromString, options = "java.util.List<T>") Closure<List<Object>> customizer) {
        this.customizer = customizer
        this
    }

}
