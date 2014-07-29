package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.modelcatalogue.core.util.ListWrapper
import pl.touk.excel.export.WebXlsxExporter

/**
 * Created by ladin on 07.04.14.
 */
class XLSXListRenderer extends AbstractRenderer<ListWrapper> {

    static final MimeType XLSX                  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    static final MimeType EXCEL                 = new MimeType('application/vnd.ms-excel', 'xlsx')
    static final XLSXRowWriter DEFAULT_WRITER   = XLSXRowWriterBuilder.writer().headers('EXPORT NOT CONFIGURED').build()

    private final Map<String, List<XLSXRowWriter>> writers = [:]

    XLSXListRenderer() {
        super(ListWrapper, [EXCEL, XLSX] as MimeType[])
    }


    @Override
    void render(ListWrapper container, RenderContext context) {
        if (!(context instanceof ServletRenderContext)) {
            throw new IllegalStateException("This renderer only works for servlet environment with ServletRendererContext")
        }

        context.setContentType(GrailsWebUtil.getContentType(XLSX.name, GrailsWebUtil.DEFAULT_ENCODING))

        XLSXRowWriter writer = findRowWriter(context.webRequest.params.report?.toString(), container, context)

        WebXlsxExporter exporter = new WebXlsxExporter()
        exporter.setResponseHeaders(context.webRequest.currentResponse, writer.getFileName(context) ?: context.controllerName)

        int counter = 0

        List<Object> headers = writer.getHeaders()

        if (headers) {
            exporter.fillRow(headers, counter++)
        }

        for(item in container.items) {
            List<List<Object>> rows = writer.getRows(item)
            for (List<Object> row in rows) {
                exporter.fillRow(row, counter++)
            }
        }

        exporter.setWorksheetName('Export')
        exporter.save(context.webRequest.currentResponse.outputStream)
    }


    public <T> XLSXRowWriter<T> findRowWriter(String name, ListWrapper<T> container, RenderContext context) {
        List<XLSXRowWriter> byName = writers.get(name)

        if (!byName) return DEFAULT_WRITER

        for (XLSXRowWriter writer in byName) {
            if (writer.isApplicableOn(container, context)) {
                return writer
            }
        }

        DEFAULT_WRITER
    }

    XLSXRowWriter registerRowWriter(XLSXRowWriter writer) {
        List<XLSXRowWriter> byName = writers.get(writer.name)
        if (byName == null) {
            byName = []
            writers[writer.name] = byName
        }
        byName.add(0, writer)
        writer
    }

    XLSXRowWriter registerRowWriter(@DelegatesTo(XLSXRowWriterBuilder) Closure definition) {
        registerRowWriter(null, definition)
    }

    XLSXRowWriter registerRowWriter(String name, @DelegatesTo(XLSXRowWriterBuilder) Closure definition) {
        XLSXRowWriterBuilder builder = XLSXRowWriterBuilder.writer(name)
        builder.with definition
        registerRowWriter(builder.build())
    }


    ListWrapper fillListWithReports(ListWrapper list, GrailsWebRequest webRequest) {
        ServletRenderContext context = new ServletRenderContext(webRequest)
        writers.each { name, writersList ->
            if (writersList.any {
                if (it == DEFAULT_WRITER) return false
                it.isApplicableOn(list, context)
            }) {
                if (name) {
                    list.availableReports << [name: name, url: context.resourcePath.contains('?') ? (context.resourcePath + '&format=xlsx&report=' + name) :  (context.resourcePath + '?format=xlsx&report=' + name)]
                } else {
                    list.availableReports << [name: name, url: context.resourcePath.contains('?') ? (context.resourcePath + '&format=xlsx') :  (context.resourcePath + '?format=xlsx')]

                }
            }
        }
        list
    }

}
