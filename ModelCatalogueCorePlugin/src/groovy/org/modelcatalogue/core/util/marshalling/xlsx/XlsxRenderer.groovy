package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import grails.util.GrailsNameUtils
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mime.MimeType
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import pl.touk.excel.export.WebXlsxExporter

/**
 * Created by ladin on 07.04.14.
 */
abstract class XLSXRenderer<T> extends AbstractRenderer<T> {

    @Autowired GrailsApplication grailsApplication

    static final MimeType XLSX  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    static final MimeType EXCEL = new MimeType('application/vnd.ms-excel', 'xlsx')

    XLSXRenderer(Class<T> type) {
        super(type, EXCEL, XLSX)
    }


    @Override
    void render(T object, RenderContext context) {
        def columns = grailsApplication.config.modelcatalogue.xlsx[context.controllerName].columns
        def headers = grailsApplication.config.modelcatalogue.xlsx[context.controllerName].headers

        if (context instanceof ServletRenderContext) {
            if (context.webRequest.params.headers) {
                headers = context.webRequest.params.headers
            }
            if (context.webRequest.params.columns) {
                columns = context.webRequest.params.columns
            }
            if (context.webRequest.parameterMap.containsKey('headers') != context.webRequest.parameterMap.containsKey('columns')) {
                context.setContentType("text/plain")
                context.status = HttpStatus.EXPECTATION_FAILED
                context.writer.println("You must specify both - headers and columns parameter")
                context.writer.close()
                context.writer.flush()
                return
            }


            columns = columns ?: getDefaultPropertiesAsString()
            headers = headers ?: getDefaultHeadersAsString()

            context.setContentType(GrailsWebUtil.getContentType(XLSX.name, GrailsWebUtil.DEFAULT_ENCODING))

            WebXlsxExporter exporter = new WebXlsxExporter()
            exporter.setResponseHeaders(context.webRequest.currentResponse, context.controllerName)
            exporter.fillHeader(headers.toString().split(/\s*,\s*/).toList())
            exporter.add(object.items, columns.toString().split(/\s*,\s*/).toList())
            exporter.save(context.webRequest.currentResponse.outputStream)
            exporter.setWorksheetName(GrailsNameUtils.getNaturalName(context.controllerName))

        } else {
            context.setContentType("text/plain")
            context.status = HttpStatus.EXPECTATION_FAILED
            context.writer.println("You must specify both - headers and columns parameter")
            context.writer.close()
            context.writer.flush()
        }
    }

    protected getDefaultHeadersAsString() {
        "ID,Name,Description"
    }

    protected getDefaultPropertiesAsString() {
        "id,name,description"
    }

}
