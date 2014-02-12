package uk.co.mc.core

class ExtensionValue extends CatalogueElement {

    /* the name property from catalogue element is a key for the extension */

    String value

    static belongsTo = [element: ExtendibleElement]

    static constraints = {
        value maxSize: 1000, nullable: true
    }


    @Override
    public String toString() {
        return "extension for ${element} (${name}=${value})";
    }
}
