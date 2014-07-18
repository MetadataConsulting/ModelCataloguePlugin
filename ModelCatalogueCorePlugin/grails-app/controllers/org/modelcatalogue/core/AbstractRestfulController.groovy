package org.modelcatalogue.core

import grails.converters.XML
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.NO_CONTENT

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']

    AssetService assetService
    SearchCatalogue modelCatalogueSearchService
    ExecutorService executorService

    XLSXListRenderer xlsxListRenderer


    AbstractRestfulController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractRestfulController(Class<T> resource) {
        super(resource, false)
    }

    def search(Integer max){
        handleParams(max)
        def results =  modelCatalogueSearchService.search(resource, params)

        if(results.errors){
            reportCapableRespond results
            return
        }

        def total = (results.total)?results.total.intValue():0

        Elements elements = new Elements(
                base: "/${resourceName}/search",
                total: total,
                items: results.searchResults
            )
        respondWithLinks elements
    }

    protected handleParams(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
            xml {
                params.max = Math.min(max ?: 10000, 10000)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }
        if (defaultSort && !params.sort)    params.sort     = defaultSort
        if (defaultOrder && !params.order)  params.order    = defaultOrder
    }

    @Override
    def index(Integer max) {
        handleParams(max)
        reportCapableRespond Lists.all(params, resource, basePath)
    }


    protected getBasePath()     { "/${resourceName}/" }
    protected getDefaultSort()  { null }
    protected getDefaultOrder() { null }


    def validate() {
        if(handleReadOnly()) {
            return
        }
        def instance = createResource(getParametersToBind())

        instance.validate()
        if (instance.hasErrors()) {
            reportCapableRespond instance.errors, view:'create' // STATUS CODE 422
            return
        }

        reportCapableRespond instance
    }

    @Override
    @Transactional
    def delete() {
        if(handleReadOnly()) {
            return
        }

        def instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        try{
            instance.delete flush:true
        }catch (DataIntegrityViolationException ignored){
            response.status = HttpServletResponse.SC_CONFLICT
            reportCapableRespond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"]) // STATUS CODE 409
            return
        } catch (Exception ignored){
            response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
            reportCapableRespond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])  // STATUS CODE 501
            return
        }

        render status: NO_CONTENT // NO CONTENT STATUS CODE
    }


    protected Map getParametersToBind() {
        Map ret = params
        if (response.format == 'json') {
            ret = request.getJSON()
        }
        ret
    }



    /**
     * @deprecated use DetachedListWrapper instead where possible
     */
    @Deprecated
    protected void respondWithLinks(Class itemType = resource, ListWrapper listWrapper) {
        if (!listWrapper.itemType) {
            listWrapper.itemType = itemType
        }
        reportCapableRespond withLinks(listWrapper)
    }

    private <T> ListWrapper<T> withLinks(ListWrapper<T> listWrapper) {
        def links = Lists.nextAndPreviousLinks(params, listWrapper.base, listWrapper.total)
        listWrapper.previous = links.previous
        listWrapper.next = links.next
        listWrapper.offset = params.int('offset') ?: 0
        listWrapper.page = params.int('max') ?: 10
        listWrapper.sort = params.sort ?: defaultSort
        listWrapper.order = params.order ?: defaultOrder
        listWrapper
    }

    /**
     * @deprecated This method is no longer used by index action
     */
    @Override @Deprecated
    protected List<T> listAllResources(Map params) {
        return super.listAllResources(params)
    }

    /**
     * @deprecated This method is no longer used by index action
     */
    @Override @Deprecated
    protected Integer countResources() {
        return super.countResources()
    }

    protected void reportCapableRespond(Map args, Object value) {
        reportCapableRespond((Object)value, (Map) args)
    }

    protected void reportCapableRespond(Object value) {
        reportCapableRespond((Object)value, (Map)[:])
    }
    /**
     * Respond which is able to capture XML exports to asset if URL parameter is {@code asset=true}.
     * @param param object to be rendered
     */
    protected void reportCapableRespond(Object param, Map args) {
        if (params.format == 'xml' && params.boolean('asset')) {
            Asset asset = renderXMLAsAsset (param as XML)

            webRequest.currentResponse.with {
                def location = g.createLink(controller: 'asset', id: asset.id, action: 'show')
                status = 302
                setHeader("Location", location.toString())
                setHeader("X-Asset-ID", asset.id.toString())
                outputStream.flush()
            }
        } else {
            respond((Object)param, (Map) args)
        }
    }

    protected Asset renderXMLAsAsset(XML xml) {
        String theName = (params.name ?: params.action)

        String uri = request.forwardURI + '?' + request.queryString

        Asset asset = new Asset(
                name: theName,
                originalFileName: theName,
                description: "Your export will be available in this asset soon. Use Refresh action to reload.",
                status: PublishedElementStatus.PENDING,
                contentType: 'application/xml',
                size: 0
        )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id

        executorService.submit {
            try {
                Asset updated = Asset.get(id)

                assetService.storeAssetWithSteam(updated, 'application/xml') { OutputStream out ->
                    xml.render(new OutputStreamWriter(out, 'UTF-8'))
                }

                updated.status = PublishedElementStatus.FINALIZED
                updated.description = "Your export is ready. Use Download button to view it."
                updated.save(flush: true, failOnError: true)
                updated.ext['Original URL'] = uri
            } catch (e) {
                log.error "Exception of type ${e.class} exporting asset ${id}", e
                throw e
            }
        }

        asset
    }

}
