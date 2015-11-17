package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap

class Document {

    final String type
    final String id
    final ImmutableMap<String, Object> payload

    Document(String type, String id, ImmutableMap<String, Object> payload) {
        this.type = type
        this.id = id
        this.payload = payload
    }
}
