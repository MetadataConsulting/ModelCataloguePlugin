package org.modelcatalogue.core.util.marshalling

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.springframework.beans.factory.annotation.Autowired

class AssetMarshaller extends CatalogueElementMarshaller {

    @Autowired LinkGenerator linkGenerator

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

        if (el.status == ElementStatus.PENDING) {
            BuildProgressMonitor monitor = BuildProgressMonitor.get(el.id)

            if (monitor) {
                ret.htmlPreview = """
                    <pre>${monitor.lastMessages}</pre>
                """
            }

        }

        ret
    }

}




