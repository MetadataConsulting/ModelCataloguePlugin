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


    public boolean equals(Object obj) {
        if (!(obj instanceof ExtendibleElement)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        ExtendibleElement ce = (ExtendibleElement) obj;
        return new EqualsBuilder()
                .append(name, ce?.name)
                .append(versionNumber, ce?.versionNumber)
                .append(extensions, ce?.extensions)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(versionNumber)
                .append(extensions)
                .toHashCode();
    }


}
