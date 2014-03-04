package uk.co.mc.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExtensionValue extends CatalogueElement {

    /* the name property from catalogue element is a key for the extension */

    static searchable = {
        name boost:5
        incomingRelationships component: true
        outgoingRelationships component: true
        except = ['element']
    }

    String extensionValue

    static belongsTo = [element: ExtendibleElement]

    static constraints = {
        extensionValue maxSize: 1000, nullable: true
    }


    @Override
    public String toString() {
        return "extension for ${element} (${name}=${extensionValue})";
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
                .append(name, ce.name)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }


}
