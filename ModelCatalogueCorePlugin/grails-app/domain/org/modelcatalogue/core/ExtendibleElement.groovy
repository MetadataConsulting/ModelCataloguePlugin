package org.modelcatalogue.core

import org.modelcatalogue.core.util.ExtendibleElementExtensionsWrapper

/*
*
* Data Elements and Models are extendible elements. This allows them to add additional metadata as properties
*
* */

abstract class ExtendibleElement extends PublishedElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        extensions component:true
        except = ['ext']
    }

    static hasMany = [extensions: ExtensionValue]
    static transients = ['ext']

    Map<String, String> ext = new ExtendibleElementExtensionsWrapper(this)

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, extensions: ${extensions}]"
    }


}
