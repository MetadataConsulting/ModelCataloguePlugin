package org.modelcatalogue.core

interface Extendible {

    Set<Extension> listExtensions()

    Extension addExtension(String name, String value)
    void removeExtension(Extension extension)

}