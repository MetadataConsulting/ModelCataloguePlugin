package org.modelcatalogue.core.reports

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.reports.ReportDescriptor.RenderType
import org.modelcatalogue.core.util.lists.ListWrapper

class ReportDescriptorBuilder {

    private ReportDescriptor descriptor = new ReportDescriptor()


    ReportDescriptorBuilder title(String title) {
        descriptor.title = { title }  ; this
    }

    ReportDescriptorBuilder title(Closure title) {
        descriptor.title = title  ; this
    }

    ReportDescriptorBuilder defaultName(String defaultName) {
        descriptor.defaultName = { defaultName } ; this
    }

    ReportDescriptorBuilder defaultName(Closure defaultName) {
        descriptor.defaultName = defaultName ; this
    }

    ReportDescriptorBuilder hasExportDepth(Boolean hasExportDepth) {
        descriptor.hasExportDepth = { hasExportDepth } ; this
    }

    ReportDescriptorBuilder hasExportDepth(Closure hasExportDepth) {
        descriptor.hasExportDepth = hasExportDepth ; this
    }

    ReportDescriptorBuilder type(Class... types) {
        descriptor.conditions << { types.any { type -> type.isAssignableFrom(it.class) }} ; this
    }

    ReportDescriptorBuilder item(Class item) {
        type ListWrapper
        descriptor.conditions << { (it as ListWrapper).itemType && item.isAssignableFrom((it as ListWrapper).itemType)} ; this
    }

    ReportDescriptorBuilder when(Closure when) {
        descriptor.conditions << when ; this
    }

    ReportDescriptorBuilder and(Closure when) {
        descriptor.conditions << when ; this
    }

    ReportDescriptorBuilder uri(String uri) {
        descriptor.linkParams = {[(LinkGenerator.ATTRIBUTE_URI): uri]} ; this
    }


    ReportDescriptorBuilder url(String url) {
        descriptor.linkParams = {[(LinkGenerator.ATTRIBUTE_URL): url]} ; this
    }

    ReportDescriptorBuilder link(Map<String, Object> linkParams) {
        def copy = new HashMap(linkParams)
        descriptor.linkParams = { copy } ; this
    }

    ReportDescriptorBuilder link(Closure<Map<String, Object>> linkParams) {
        descriptor.linkParams = linkParams ; this
    }

    ReportDescriptor build(LinkGenerator generator) {
        if (!descriptor.title) throw new IllegalStateException("The descriptor is missing it's title")
        if (!descriptor.linkParams) throw new IllegalStateException("The descriptor is missing it's link parameters")

        descriptor.generator = generator
        descriptor
    }

    ReportDescriptorBuilder creates(RenderType type) {
        descriptor.renderType = type ; this
    }

    static RenderType getAsset() { RenderType.ASSET }
    static RenderType getLink()  { RenderType.LINK  }
}
