package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import grails.util.GrailsWebUtil
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.ListWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.RequestContextHolder
import pl.touk.excel.export.WebXlsxExporter

import java.util.concurrent.ExecutorService

/**
 * Created by ladin on 07.04.14.
 */
@Log4j
class XLSXListRenderer extends AbstractRenderer<ListWrapper> {

	static String DEFAULT_LAYOUT_RESOURCENAME   = "/excelLayouts/defaultLayout.xlsx"
    static final MimeType XLSX                  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    static final MimeType EXCEL                 = new MimeType('application/vnd.ms-excel', 'xlsx')
    static final XLSXRowWriter DEFAULT_WRITER   = XLSXRowWriterBuilder.writer().headers('EXPORT NOT CONFIGURED').build()

    private final Map<String, List<XLSXRowWriter>> writers = [:]

    @Autowired ReportsRegistry reportsRegistry
    @Autowired ExecutorService executorService
    @Autowired LinkGenerator linkGenerator
    @Autowired AssetService assetService
    @Autowired AuditService auditService
    @Autowired SecurityService modelCatalogueSecurityService

    XLSXListRenderer() {
        super(ListWrapper, [EXCEL, XLSX] as MimeType[])
    }


    @Override
    void render(ListWrapper container, RenderContext context) {
        if (!(context instanceof ServletRenderContext)) {
            throw new IllegalStateException("This renderer only works for servlet environment with ServletRendererContext")
        }

        XLSXRowWriter writer = findRowWriter(context.webRequest.params.report?.toString(), container, context)

        if (modelCatalogueSecurityService.userLoggedIn) {
            renderAsAsset(container, writer, context)
        } else {
            renderDirectly(container, writer, context)
        }
    }

    private static void renderDirectly(container, XLSXRowWriter writer, ServletRenderContext context) {
        context.setContentType(GrailsWebUtil.getContentType(XLSX.name, GrailsWebUtil.DEFAULT_ENCODING))

        String layoutFileName = getLayoutResourceFileName()

        WebXlsxExporter exporter
        if(layoutFileName){
            exporter = new WebXlsxExporter(layoutFileName)
        }else{
            exporter = new WebXlsxExporter()
        }

        //it should be set, before adding any row
        exporter.setWorksheetName('Export')


        exporter.setResponseHeaders(context.webRequest.currentResponse, extractName(writer, context))

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

    private void renderAsAsset(container, XLSXRowWriter writer, ServletRenderContext context) {
        String layoutFileName = getLayoutResourceFileName()

        String theName = extractName(writer, context)

        Asset asset = new Asset(
                name: theName,
                originalFileName: theName,
                description: "Your export will be available in this asset soon. Use Refresh action to reload",
                status: ElementStatus.PENDING,
                contentType: XLSX.name,
                size: 0
        )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id

        Long authorId = modelCatalogueSecurityService.currentUser?.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
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

                    if (writer.appendingMetadata) {
                        Map<String, Integer> headers2index = [:]
                        List<List<Object>> rowsToWrite = [headers]
                        for (item in container.items) {
                            List<List<Object>> rows = writer.getRows(item)
                            for (List<Object> row in rows) {
                                List<Object> extendibleRow = row.withDefault { "" }
                                if (item instanceof Extendible) {
                                    item.listExtensions().each {
                                        Integer index = headers2index[it.name]
                                        if (index == null) {
                                            index = headers.size()
                                            headers.add(it.name)
                                            headers2index.put(it.name, index)
                                        }
                                        extendibleRow[index] = it.extensionValue
                                    }
                                }
                                rowsToWrite << extendibleRow
                            }
                        }

                        for (row in rowsToWrite) {
                            exporter.fillRow(row, counter++)
                        }
                    } else {
                        if (headers) {
                            exporter.fillRow(headers, counter++)
                        }

                        for (item in container.items) {
                            List<List<Object>> rows = writer.getRows(item)
                            for (List<Object> row in rows) {
                                exporter.fillRow(row, counter++)
                            }
                        }
                    }

                    Asset updated = Asset.get(id)

                    assetService.storeAssetWithSteam(updated, XLSX.name) {
                        exporter.save(it)
                    }

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your export is ready. Use Download button to view it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} exporting asset ${id}", e
                    throw e
                }
            }
        }

        context.webRequest.currentResponse.with {
            setHeader("X-Asset-ID", asset.id.toString())
            sendRedirect(linkGenerator.link(controller: 'asset', id: asset.id, action: 'show'))
        }
    }

    private static String extractName(XLSXRowWriter writer, ServletRenderContext context) {
        String theName = writer.getFileName(context)

        if (!theName && writer.title) theName = writer.title
        if (!theName && writer.name) theName = writer.name
        if (!theName) theName = context.controllerName ?: 'export'

        if (!theName.endsWith('.xlsx')) theName += '.xlsx'
        theName
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
                GrailsWebRequest webRequest = RequestContextHolder.currentRequestAttributes() as GrailsWebRequest
                ServletRenderContext context = new ServletRenderContext(webRequest)
                writer.isApplicableOn(it, context)
            }
            link {
                GrailsWebRequest webRequest = RequestContextHolder.currentRequestAttributes() as GrailsWebRequest
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
