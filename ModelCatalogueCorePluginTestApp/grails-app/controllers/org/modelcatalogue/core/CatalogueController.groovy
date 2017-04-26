package org.modelcatalogue.core

import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.util.Environment
import grails.util.GrailsNameUtils
import groovy.json.JsonBuilder
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.cytoscape.json.CatalogueCytoscapeJsonPrinter
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.modelcatalogue.core.xml.EscapeSpecialWriter
import org.springframework.http.HttpStatus

class CatalogueController {

    def dataModelService
    def dataClassService
    def elementService
    def initCatalogueService
    def modelCatalogueSecurityService
    def executorService

    /** Does what? Must ask Vlad.
     * Vlad: Used for cross-references or simple references.
     * Creates XML for Export XML action,
     * and Edit XSD action (which is only available for Data Classes) */
    def xref() {
        // what is this magic? Somehow getting the currently looked at element.
        CatalogueElement element = elementService.findByModelCatalogueId(CatalogueElement, request.forwardURI.replace('/export', ''))

        if (!params.resource || !element) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            // What is that regex?
            // What are these special characters s: and s1:?
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            Writable w = printer.bind(element){ // bind element with the following as configuration for the PrintContext
                idIncludeVersion = true
                if (params.full != 'true') {
                    keepInside = element.instanceOf(DataModel) ? element : element.dataModel
                }
                if (params.repetitive == 'true') {
                    repetitive = true
                }
            }
            w.writeTo(response.writer)
            return
        }

        // What is resource?
        redirect controller: params.resource, action: 'show', id: element.id
    }

    /** Method to create JSON file for cytoscape to display a graph of the element.
     * Adapted from xref, using something similar to CatalogueXmlPrinter, etc.
     * At the moment, simply finding the elements related by Hierarchy and Containment within a class,
     * displaying those relations.
     * Next step: metadata.*/
    def cytoscape_json() {
        // what is this magic? Somehow getting the currently looked at element.
        CatalogueElement element = elementService.findByModelCatalogueId(CatalogueElement, request.forwardURI.replace('/cytoscapeJsonExport', ''))

        if (!params.resource || !element) {
            render status: HttpStatus.NOT_FOUND
            return
        }
        response.contentType = 'application/json'
        // What is that regex?
        // What are these special characters s: and s1:? IDE-interpolated argument names.
        response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.cytoscape.json\"")
        CatalogueCytoscapeJsonPrinter printer = new CatalogueCytoscapeJsonPrinter(dataModelService, dataClassService)
        JsonBuilder builder = printer.bind(element){ // bind element with the following as configuration for the PrintContext
            idIncludeVersion = true
            if (params.full != 'true') {
                keepInside = element.instanceOf(DataModel) ? element : element.dataModel
            }
        }
        // EscapeSpecialWriter escapeSpecialWriter = new EscapeSpecialWriter(response.writer)
        response.writer.out.append(builder.toPrettyString())
        // builder.writeTo(response.writer)
        response.writer.flush()
        return
        // What is resource?
        // redirect controller: params.resource, action: 'show', id: element.id
    }

    /** Vlad: Did something similar to xref().
     * In early times it was used to fetch element by extension (i.e. metadata).
     * e.g. HPO elements could be fetched by /ext/HPO_ID/HP_123445.
     * That is, if an element had metadata HPO_ID:HP_123445 it would be found by this method.
     * And then it would be output in XML.
     * It seemed like a nice feature to have but it's never been used.
     * It could probably be removed without breaking anything.*/
    def ext() {
        String key = params.key
        String value = params.value

        DetachedCriteria<CatalogueElement> criteria = new DetachedCriteria<CatalogueElement>(CatalogueElement)
        criteria = criteria.build {
            extensions {
                eq 'name', key
                eq 'extensionValue', value
            }
            sort('versionNumber', 'desc')
        }

        List<CatalogueElement> elements = criteria.list()

        if (!elements) {
            render status: HttpStatus.NOT_FOUND
            return
        }


        CatalogueElement element = elements.first()

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element){
                if (params.full != 'true') {
                    keepInside = element.instanceOf(DataModel) ? element : element.dataModel
                }
                if (params.repetitive == 'true') {
                    repetitive = true
                }
            }.writeTo(response.writer)
            return
        }

        redirect url: "${grailsApplication.config.grails.serverURL}/catalogue/${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.id}"
    }

    /** Shows progress of importing catalogues or publishing/finalising drafts. */
    def feedback(String key) {
        render(BuildProgressMonitor.get(key) as JSON)
    }

    /** Same as feedback but fetches all the feedback monitors? */
    def feedbacks() {
        if (params.max) {
            params.max = params.long('max')
        }
        render(Lists.lazy(params, ProgressMonitor, '/feedback', {
            CacheService.MONITORS_CACHE.asMap().entrySet().sort{ a, b -> -(a.value.lastUpdated <=> b.value.lastUpdated) }.collect { [key: it.key, name: it.value.name, status: it.value.status.toElementStatusEquivalent().toString(), ] }
        }, {
            CacheService.MONITORS_CACHE.size()
        }) as JSON)
    }

    /** Presents options for preloading database from XML.
     * Used when running blank catalogue. */
    def dataModelsForPreload() {
        // only render data models for preload if there is no data model in the catalogue (very likely the first run)
        if (DataModel.findByNameNotEqual('Clinical Tags') || !modelCatalogueSecurityService.hasRole(UserService.ROLE_ADMIN)) {
            render([] as JSON)
            return

        }

        render((grailsApplication.config.mc.preload ?: []) as JSON)
    }

    /** Actually performs the catalogue import from an XML file */
    def importFromUrl() {
        def urls = request.JSON.urls

        if (!urls) {
            render status: HttpStatus.BAD_REQUEST
            return
        }

        String logId = System.currentTimeMillis()
        BuildProgressMonitor monitor = BuildProgressMonitor.create('Import Sample', logId)

        executorService.submit {
            try {
                initCatalogueService.importXMLFromURLs(urls?.collect{ new URL(it) }, false, monitor)
                monitor.onCompleted()
            } catch (e) {
                monitor.onError(e)
            }
        }

        render([id: logId] as JSON)
    }

}
