package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.Asset

class AssetMarshaller extends ExtendibleElementMarshallers {

    LinkGenerator linkGenerator

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
                md5: el.md5
        )
        if (el.md5) {
            ret.downloadUrl = linkGenerator.link(controller: 'asset', action: 'download', id: el.id, absolute: true)
        }
        ret
    }

    @Override
    protected void addXmlAttributes(el, XML xml) {
        super.addXmlAttributes(el, xml)
        addXmlAttribute(el.contentType, "contentType", xml)
        addXmlAttribute(el.originalFileName, "originalFileName", xml)
        addXmlAttribute(el.md5, "md5", xml)
        if (el.md5) {
            addXmlAttribute(linkGenerator.link(controller: 'asset', action: 'download', id: el.id, absolute: true), 'downloadUrl', xml)
        }
        addXmlAttribute(el.size, "size", xml)

    }

}




