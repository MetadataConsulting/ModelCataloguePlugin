package org.modelcatalogue.core

class ExtensionValue implements Extension {

    /* the name property from catalogue element is a key for the extension */

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        except = ['element']
    }

    String name
    String extensionValue

    static belongsTo = [element: CatalogueElement]

    static constraints = {
        name size: 1..255
        extensionValue maxSize: 2000, nullable: true
    }


    @Override
    public String toString() {
        return "extension for ${element} (${name}=${extensionValue})"
    }


}
