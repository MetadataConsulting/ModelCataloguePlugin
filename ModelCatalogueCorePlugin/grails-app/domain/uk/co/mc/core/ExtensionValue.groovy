package uk.co.mc.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

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


    public boolean equals(Object obj) {
        if (!(obj instanceof ExtensionValue)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        ExtensionValue ce = (ExtensionValue) obj;
        return new EqualsBuilder()
                .append(name, ce?.name)
                .append(value, ce?.value)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(value)
                .toHashCode();
    }


}
