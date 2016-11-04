package org.modelcatalogue.core.elasticsearch

import groovy.transform.CompileStatic
import org.elasticsearch.action.bulk.BulkItemResponse

@CompileStatic
class SimpleIndexResponse {
    final String index
    final String type
    final String id
    final boolean ok

    SimpleIndexResponse(String index, String type, String id, boolean ok) {
        this.index = index
        this.type = type
        this.id = id
        this.ok = ok
    }

    static SimpleIndexResponse from(BulkItemResponse bulkItemResponse) {
        return new SimpleIndexResponse(
                bulkItemResponse.index,
                bulkItemResponse.type,
                bulkItemResponse.id,
                !bulkItemResponse.failed
        )
    }
}
