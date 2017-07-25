package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap

/*
* Documents are the json that is sent to elasticsearch
*
* */

class Document implements Serializable {

    static final Document EMPTY = new Document('','',0, ImmutableMap.of())
    final String type
    final String id
    final Long version
    final ImmutableMap<String, Object> payload

    Document(String type, String id, Long version, ImmutableMap<String, Object> payload) {
        this.type = type
        this.id = id
        this.version = version
        this.payload = payload
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Document document = (Document) o

        if (id != document.id) return false
        if (version != document.version) return false
        if (type != document.type) return false

        return true
    }

    int hashCode() {
        int result
        result = (type != null ? type.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }
}
