package org.modelcatalogue.core

interface Extendible<E extends Extension> {

    Set<E> listExtensions()

    E addExtension(String name, String value)
    E updateExtension(E old, String value)
    E findExtensionByName(String name)

    int countExtensions()
    void removeExtension(E extension)

}