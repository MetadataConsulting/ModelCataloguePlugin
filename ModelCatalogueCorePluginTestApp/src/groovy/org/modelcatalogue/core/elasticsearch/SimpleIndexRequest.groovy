package org.modelcatalogue.core.elasticsearch

import groovy.transform.CompileStatic
/*
*
*     creates the names of the the indices and then creates teh document associated with entities
*     indices are usually one for global search and one for data model search
* */
@CompileStatic
class SimpleIndexRequest implements Serializable {

    final Set<String> indices
    final Document document

    SimpleIndexRequest(Set<String> indices, Document document) {
        this.indices = indices
        this.document = document
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SimpleIndexRequest that = (SimpleIndexRequest) o

        if (document != that.document) return false
        if (indices != that.indices) return false

        return true
    }

    int hashCode() {
        int result
        result = (indices != null ? indices.hashCode() : 0)
        result = 31 * result + (document != null ? document.hashCode() : 0)
        return result
    }
}
