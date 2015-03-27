package org.modelcatalogue.core

class ExtensionValue implements Extension {


    String name
    String extensionValue

    Long index = System.currentTimeMillis()

    static belongsTo = [element: CatalogueElement]

    static constraints = {
        name size: 1..255
        extensionValue maxSize: 2000, nullable: true
    }

    static mapping = {
        index name: '`index`'
    }

    @Override
    public String toString() {
        return "extension for ${element} (${name}=${extensionValue})"
    }


}
