package org.modelcatalogue.core.util

interface ListWithTotalAndType<T> extends ListWithTotal<T>{
    Class<T> getItemType()
}
