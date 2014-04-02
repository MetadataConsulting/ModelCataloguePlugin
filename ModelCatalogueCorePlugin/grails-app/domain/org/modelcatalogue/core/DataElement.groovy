package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder


/*
* A data element is an atomic unit of data
* i.e. xml  <xs:element name="title" />
*
* */

class DataElement extends ExtendibleElement {

    String code

    static constraints = {
        code nullable:true, unique:true, maxSize: 255
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        extensions component:true
        code boost:5
        incomingRelationships component: true
        outgoingRelationships component: true
        except =  ['containedIn', 'instantiatedBy']
    }

    static relationships = [
            incoming: [containment: 'containedIn'],
            outgoing: [instantiation: 'instantiatedBy']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, code: ${code}, version: ${version}, status: ${status}]"
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof DataElement)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        DataElement de = (DataElement) obj;
        return new EqualsBuilder()
                .append(name, de.name)
                .append(code, de.code)
                .append(versionNumber, de.versionNumber)
                .append(extensions, de.extensions)
                .isEquals()
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(code)
                .append(versionNumber)
                .append(extensions)
                .toHashCode()
    }


}
