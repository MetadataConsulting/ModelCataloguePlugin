package org.modelcatalogue.core.util.lists

interface HasListWrapper<T> {

    ListWrapper<T> getList()
    void setList(ListWrapper<T> list)

}
