package uk.co.mc.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import uk.co.mc.core.util.ExtendibleElementExtensionsWrapper

/*
*
* Data Elements and Models are extendible elements. This allows them to add additional metadata as properties
*
* */

abstract class ExtendibleElement extends PublishedElement{


    static hasMany = [extensions: ExtensionValue]
    static transients = ['ext']

    Map<String, String> ext = new ExtendibleElementExtensionsWrapper(this)

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, extensions: ${extension}]"
    }


}
