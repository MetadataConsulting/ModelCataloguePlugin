package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.Asset

class AssetDocumentSerializer extends CatalogueElementDocumentSerializer<Asset> {

    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, Asset element, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, element, builder)

        safePut(builder, 'size', element.size)
        safePut(builder, 'content_type', element.contentType)
        safePut(builder, 'md5', element.md5)
        safePut(builder, 'original_file_name', element.originalFileName)
        return builder
    }
}
