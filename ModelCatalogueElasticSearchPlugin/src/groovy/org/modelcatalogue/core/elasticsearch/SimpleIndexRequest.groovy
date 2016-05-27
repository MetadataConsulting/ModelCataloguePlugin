package org.modelcatalogue.core.elasticsearch

import groovy.transform.CompileStatic

@CompileStatic
class SimpleIndexRequest {

    final Set<String> indices
    final Document document
    final Set<Class> mappedClasses

    SimpleIndexRequest(Set<String> indices, Document document, Set<Class> mappedClasses) {
        this.indices = indices
        this.document = document
        this.mappedClasses = mappedClasses
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
