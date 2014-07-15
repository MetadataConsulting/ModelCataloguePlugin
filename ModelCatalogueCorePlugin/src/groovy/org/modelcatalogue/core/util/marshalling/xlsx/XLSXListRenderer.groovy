package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.ListWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.RequestContextHolder
import pl.touk.excel.export.WebXlsxExporter

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

/**
 * Created by ladin on 07.04.14.
 */
@Log4j
class XLSXListRenderer extends AbstractRenderer<ListWrapper> {

	static String DEFAULT_LAYOUT_RESOURCENAME       = "/excelLayouts/defaultLayout.xlsx"
    static final MimeType XLSX                  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    static final MimeType EXCEL                 = new MimeType('application/vnd.ms-excel', 'xlsx')
    static final XLSXRowWriter DEFAULT_WRITER   = XLSXRowWriterBuilder.writer().headers('EXPORT NOT CONFIGURED').build()

    private final Map<String, List<XLSXRowWriter>> writers = [:]

    @Autowired ReportsRegistry reportsRegistry
    @Autowired ExecutorService executorService
    @Autowired LinkGenerator linkGenerator
    @Autowired AssetService assetService

    XLSXListRenderer() {
        super(ListWrapper, [EXCEL, XLSX] as MimeType[])
    }


    @Override
    void render(ListWrapper container, RenderContext context) {
        if (!(context instanceof ServletRenderContext)) {
            throw new IllegalStateException("This renderer only works for servlet environment with ServletRendererContext")
        }

        XLSXRowWriter writer = findRowWriter(context.webRequest.params.report?.toString(), container, context)

 		String layoutFileName = getLayoutResourceFileName()

        String theName = writer.getFileName(context)

        if (!theName && writer.title) theName = writer.title
        if (!theName && writer.name) theName = writer.name
        if (!theName) theName = context.controllerName ?: 'export'

        if (!theName.endsWith('.xlsx')) theName += '.xlsx'

        Asset asset = new Asset(
            name: theName,
            originalFileName: theName,
            description: "Your export will be available in this asset soon. Use Refresh action to reload",
            status: PublishedElementStatus.PENDING,
            contentType: XLSX.name,
            size: 0
        )

        asset.save(flush: true, failOnError: true)
        
        Long id = asset.id

        Future futureAsset = executorService.submit {
            try {
                WebXlsxExporter exporter
                if (layoutFileName) {
                    exporter = new WebXlsxExporter(layoutFileName)
                } else {
                    exporter = new WebXlsxExporter()
                }

                exporter.setWorksheetName('Export')

                int counter = 0

                List<Object> headers = writer.getHeaders()

                if (headers) {
                    exporter.fillRow(headers, counter++)
                }

                for (item in container.items) {
                    List<List<Object>> rows = writer.getRows(item)
                    for (List<Object> row in rows) {
                        exporter.fillRow(row, counter++)
                    }
                }


                Asset updated = Asset.get(id)

                assetService.storeAssetWithSteam(updated, XLSX.name) {
                    exporter.save(it)
                }

                updated.status = PublishedElementStatus.FINALIZED
                updated.description = "Your export is ready. Use Download button to view it."
                updated.save(flush: true, failOnError: true)
            } catch (e) {
                log.error "Exception of type ${e.class} exporting asset ${id}", e
                throw e
            }
        }


        // mainly for testing purposes
        if (context.webRequest.params.boolean('_x_wait_for_completion')) {
            futureAsset.get()
        }

        context.webRequest.currentResponse.with {
            def link = linkGenerator.link(controller: 'asset', id: asset.id, action: 'show')
            status = 302
            setHeader("Location", link)
            setHeader("X-Asset-ID", asset.id.toString())
            // outputStream << link
            outputStream.flush()
        }

    }


    private static String getLayoutResourceFileName() {
        URL layoutResource = XLSXListRenderer.getResource(DEFAULT_LAYOUT_RESOURCENAME)

        if (layoutResource && layoutResource.file && (new File(layoutResource.file).exists())) {
            return layoutResource.file;
        }

        return null
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
            creates asset
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
