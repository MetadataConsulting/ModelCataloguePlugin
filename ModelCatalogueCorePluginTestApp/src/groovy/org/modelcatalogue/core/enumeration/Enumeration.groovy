package org.modelcatalogue.core.enumeration

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic class Enumeration {

    final Long id
    final String key
    final String value
    final boolean deprecated

    @PackageScope static Enumeration create(Long id, String key, String value, boolean deprecated = false) {
        return new Enumeration(id, key, value, deprecated)
    }

    private Enumeration(Long id, String key, String value, boolean deprecated) {
        this.id = id
        this.key = key
        this.value = value
        this.deprecated = deprecated
    }

    @Override
    public String toString() {
        return "[${id.toString().padLeft(5, '0')}] $key: $value${deprecated ? ' (deprecated)' : ''}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Enumeration that = (Enumeration) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return id.hashCode()
    }
}
