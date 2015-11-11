package org.modelcatalogue.core.elasticsearch

interface DocumentSerializer<T> {

    Map getDocument(T object)

}