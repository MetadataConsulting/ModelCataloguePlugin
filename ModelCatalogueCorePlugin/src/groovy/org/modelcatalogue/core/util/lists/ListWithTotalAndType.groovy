package org.modelcatalogue.core.util.lists

interface ListWithTotalAndType<T> extends ListWithTotal<T>{
    Class<T> getItemType()
}
