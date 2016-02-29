package org.modelcatalogue.core.enumeration

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic class Enumeration {

    final Long id
    final String key
    final String value

    @PackageScope static Enumeration create(Long id, String key, String value) {
        return new Enumeration(id, key, value)
    }

    private Enumeration(Long id, String key, String value) {
        this.id = id
        this.key = key
        this.value = value
    }

    @Override
    public String toString() {
        return "[${id.toString().padLeft(5, '0')}] $key: $value"
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
