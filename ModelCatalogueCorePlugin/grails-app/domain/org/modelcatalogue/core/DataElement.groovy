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

    static transients = ['containedIn', 'instantiatedBy']

    static constraints = {
        code nullable:true, unique:true, maxSize: 255
    }

    static searchable = {
        name boost:5
        extensions component:true
        code boost:5
        incomingRelationships component: true
        outgoingRelationships component: true
    }

    List/*<DataElement>*/ getContainedIn() {
        getIncomingRelationsByType(RelationshipType.containmentType)
    }

    Relationship addToContainedIn(Model model) {
        createLinkFrom(model, RelationshipType.containmentType)
    }

    void removeFromContainedIn(Model model) {
        removeLinkFrom(model, RelationshipType.containmentType)
    }


    //INSTANTIATION

    List/*<ValueDomain>*/ getInstantiatedBy() {
        getOutgoingRelationsByType(RelationshipType.instantiationType)
    }

    Relationship addToInstantiatedBy(ValueDomain valueDomain) {
        createLinkTo(valueDomain, RelationshipType.instantiationType)
    }

    void removeFromInstantiatedBy(ValueDomain valueDomain) {
        removeLinkTo(valueDomain, RelationshipType.instantiationType)
    }

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
