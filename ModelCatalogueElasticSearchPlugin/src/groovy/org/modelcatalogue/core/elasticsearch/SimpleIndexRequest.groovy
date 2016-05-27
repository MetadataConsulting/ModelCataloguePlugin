package org.modelcatalogue.core.elasticsearch

import groovy.transform.CompileStatic

@CompileStatic
class SimpleIndexRequest {

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
