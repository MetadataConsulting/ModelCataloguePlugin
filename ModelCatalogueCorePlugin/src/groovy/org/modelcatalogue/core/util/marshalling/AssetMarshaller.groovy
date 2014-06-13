package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.Asset

class AssetMarshaller extends ExtendibleElementMarshallers {

    AssetMarshaller() {
        super(Asset)
    }


    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll(
                contentType: el.contentType,
                originalFileName: el.originalFileName,
                size: el.size,
                downloadUrl: el.downloadUrl
        )
        ret
    }

    @Override
    protected void addXmlAttributes(el, XML xml) {
        super.addXmlAttributes(el, xml)
        addXmlAttribute(el.contentType, "contentType", xml)
        addXmlAttribute(el.originalFileName, "originalFileName", xml)
        addXmlAttribute(el.downloadUrl, "downloadUrl", xml)
        addXmlAttribute(el.size, "size", xml)

    }

}




