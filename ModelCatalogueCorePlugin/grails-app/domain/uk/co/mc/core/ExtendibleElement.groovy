package uk.co.mc.core

/*
*
* Data Elements and Models are extendible elements. This allows them to add additional metadata as properties
*
* */

abstract class ExtendibleElement extends PublishedElement{


    static hasMany = [extensions: ExtensionValue]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, extensions: ${extension}]"
    }
}
