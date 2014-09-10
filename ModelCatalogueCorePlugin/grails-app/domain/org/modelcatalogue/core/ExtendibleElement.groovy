package org.modelcatalogue.core

import org.modelcatalogue.core.util.ExtensionsWrapper

/*
*
* Data Elements and Models are extendible elements. This allows them to add additional metadata as properties
*
* */

abstract class ExtendibleElement extends CatalogueElement implements Extendible {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        extensions component:true
    }

    static hasMany = [extensions: ExtensionValue]
    static transients = ['ext']

    static mapping = {
        tablePerHierarchy false
        extensions lazy: false
    }

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        this.ext.clear()
        this.ext.putAll(ext)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, extensions: ${extensions}]"
    }

    @Override
    Set<Extension> listExtensions() {
        extensions
    }

    @Override
    Extension addExtension(String name, String value) {
        ExtensionValue newOne = new ExtensionValue(name: name, extensionValue: value, element: this)
        newOne.save()
        assert !newOne.errors.hasErrors()
        addToExtensions(newOne)
        newOne
    }

    @Override
    void removeExtension(Extension extension) {
        if (extension instanceof ExtensionValue) {
            removeFromExtensions(extension)
            extension.delete(flush: true)
        } else {
            throw new IllegalArgumentException("Only instances of ExtensionValue are supported")
        }
    }
}
