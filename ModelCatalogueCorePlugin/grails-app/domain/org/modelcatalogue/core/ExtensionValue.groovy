package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ExtensionValue extends CatalogueElement {

    /* the name property from catalogue element is a key for the extension */

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

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


}
