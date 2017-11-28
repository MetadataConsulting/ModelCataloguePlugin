package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.UNAUTHORIZED
import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

class CatalogueController {

    def dataModelService
    def dataClassService
    def elementService
    def initCatalogueService
    def modelCatalogueSecurityService
    def executorService
    DataModelAclService dataModelAclService
    DataModelGormService dataModelGormService

    def xref() {
        CatalogueElement element = elementService.findByModelCatalogueId(CatalogueElement, request.forwardURI.replace('/export', ''))

        if (!params.resource || !element) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        if ( !dataModelAclService.hasReadPermission((element)) ) {
            response.sendError HttpServletResponse.SC_UNAUTHORIZED
            return false
        }

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element){
                idIncludeVersion = true

                if (params.full == 'false') {
                    keepInside = element.instanceOf(DataModel) ? element : element.dataModel
                }

            }.writeTo(response.writer)
            return
        }



        redirect controller: params.resource, action: 'show', id: element.id, params: params


        return
    }

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
        return
    }


    def feedback(String key) {
        BuildProgressMonitor buildProgressMonitor = BuildProgressMonitor.get(key)
        if (buildProgressMonitor != null) {
            render(buildProgressMonitor as JSON)
        }
    }

    def feedbacks() {
        if (params.max) {
            params.max = params.long('max')
        }
        render(Lists.lazy(params, ProgressMonitor, '/feedback', {
            CacheService.MONITORS_CACHE.asMap().entrySet().sort{ a, b ->
                -(a.value.lastUpdated <=> b.value.lastUpdated)
            }.collect {
                [
                        key: it.key,
                        name: it.value.name,
                        status: it.value.status.toElementStatusEquivalent().toString(),
                ]
            }
        }, {
            CacheService.MONITORS_CACHE.size()
        }) as JSON)

    }

    //TODO: Remove/find out why this is needed
    def dataModelsForPreload() {

        List<DataModel> dataModelList = dataModelGormService.findAllByNameNotEqual('Clinical Tags')
        // only render data models for preload if there is no data model in the catalogue (very likely the first run)
        if ( !dataModelList.isEmpty() /*|| !modelCatalogueSecurityService.hasRole(UserService.ROLE_ADMIN)*/) {
            render([] as JSON)
            return
        }

        render((grailsApplication.config.mc.preload ?: []) as JSON)
    }

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



    protected DataModel getDataModel(CatalogueElement ce){
        if(ce instanceof DataModel) return ce
        return ce.dataModel
    }


}
