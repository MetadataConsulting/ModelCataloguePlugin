package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.Asset

class AssetDocumentSerializer extends CatalogueElementDocumentSerializer implements DocumentSerializer<Asset> {

    Map getDocument(Asset element) {
        Map ret = super.getDocument(element)

        ret.size = element.size
        ret.content_type = element.contentType
        ret.md5 = element.md5
        ret.original_file_name = element.originalFileName

        return ret
    }

}
