package org.modelcatalogue.core.elasticsearch

import groovy.transform.CompileStatic

@CompileStatic
class SimpleIndexRequest {

    final String index
    final Document document
    final Set<Class> mappedClasses

    SimpleIndexRequest(String index, Document document, Set<Class> mappedClasses) {
        this.index = index
        this.document = document
        this.mappedClasses = mappedClasses
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SimpleIndexRequest that = (SimpleIndexRequest) o

        if (document != that.document) return false
        if (index != that.index) return false

        return true
    }

    int hashCode() {
        int result
        result = (index != null ? index.hashCode() : 0)
        result = 31 * result + (document != null ? document.hashCode() : 0)
        return result
    }
}
