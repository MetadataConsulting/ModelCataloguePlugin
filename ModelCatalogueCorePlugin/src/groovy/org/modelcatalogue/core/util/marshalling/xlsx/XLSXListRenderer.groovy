package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.ListWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.RequestContextHolder
import pl.touk.excel.export.WebXlsxExporter

/**
 * Created by ladin on 07.04.14.
 */
class XLSXListRenderer extends AbstractRenderer<ListWrapper> {

	static String DEFAULT_LAYOUT_RESOURCENAME       = "/excelLayouts/defaultLayout.xlsx"
    static final MimeType XLSX                  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    static final MimeType EXCEL                 = new MimeType('application/vnd.ms-excel', 'xlsx')
    static final XLSXRowWriter DEFAULT_WRITER   = XLSXRowWriterBuilder.writer().headers('EXPORT NOT CONFIGURED').build()

    private final Map<String, List<XLSXRowWriter>> writers = [:]

    @Autowired ReportsRegistry reportsRegistry

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

 		URL layoutResource = this.class.getResource(DEFAULT_LAYOUT_RESOURCENAME)
		String layoutFileName

		if(layoutResource && layoutResource.file && (new File(layoutResource.file).exists())){
			layoutFileName = layoutResource.file;
		}

		WebXlsxExporter exporter
		if(layoutFileName){
			exporter = new WebXlsxExporter(layoutFileName)
		}else{
			exporter = new WebXlsxExporter()
		}

		//it should be set, before adding any row
		exporter.setWorksheetName('Export')


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

        exporter.save(context.webRequest.currentResponse.outputStream)
    }


    public <T> XLSXRowWriter<T> findRowWriter(String name, ListWrapper<T> container, RenderContext context) {
        List<XLSXRowWriter> byName = writers.get(name ?: null)

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
        XLSXRowWriter writer = registerRowWriter(builder.build())

        reportsRegistry.register {
            title writer.title
            type ListWrapper
            when {
                GrailsWebRequest webRequest = RequestContextHolder.currentRequestAttributes()
                ServletRenderContext context = new ServletRenderContext(webRequest)
                writer.isApplicableOn(it, context)
            }
            link {
                GrailsWebRequest webRequest = RequestContextHolder.currentRequestAttributes()
                Map params = [:]
                params.putAll(webRequest.params)
                params.format = 'xlsx'
                params.report = writer.name
                params.remove('sort')
                params.remove('order')
                params.remove('max')
                params.remove('offset')
                [controller: webRequest.controllerName, action: webRequest.actionName, params: params]
            }
        }

        writer
    }

}
