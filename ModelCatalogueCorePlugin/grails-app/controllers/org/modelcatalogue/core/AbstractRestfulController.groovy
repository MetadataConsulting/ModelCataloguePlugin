package org.modelcatalogue.core

import grails.converters.XML
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']

    AssetService assetService
    SearchCatalogue modelCatalogueSearchService
    SecurityService modelCatalogueSecurityService
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
        def instance = createResource()

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
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notAuthorized()
            return
        }

        if(handleReadOnly()) {
            return
        }

        def instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        checkAssociationsBeforeDelete(instance)

        if (instance.hasErrors()) {
            reportCapableRespond instance.errors
            return
        }

        try{
            instance.delete flush:true
        }catch (DataIntegrityViolationException ignored){
            response.status = HttpServletResponse.SC_CONFLICT
            reportCapableRespond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, ignored.message]) // STATUS CODE 409
            return
        } catch (Exception ignored){
            response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
            reportCapableRespond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])  // STATUS CODE 501
            return
        }

        render status: NO_CONTENT // NO CONTENT STATUS CODE
    }

    protected checkAssociationsBeforeDelete(T instance) {
        GrailsDomainClass domainClass = grailsApplication.getDomainClass(resource.name)

        for (GrailsDomainClassProperty property in domainClass.persistentProperties) {
            if ((property.oneToMany || property.manyToMany) && instance.hasProperty(property.name) ){
                def value = instance[property.name]
                if (value) {
                    instance.errors.rejectValue property.name, "delete.association.before.delete.entity.${property.name}", "You must remove all ${property.naturalName.toLowerCase()} before you delete this element"
                }
            }
        }
    }

    protected String getRoleForSaveAndEdit() {
        'CURATOR'
    }

    /**
     * Saves a resource
     */
    @Transactional
    def save() {
        if (!modelCatalogueSecurityService.hasRole(roleForSaveAndEdit)) {
            notAuthorized()
            return
        }
        if(handleReadOnly()) {
            return
        }
        def instance = createResource()

        instance.validate()
        if (instance.hasErrors()) {
            respond instance.errors, view:'create' // STATUS CODE 422
            return
        }

        cleanRelations(instance)

        instance.save flush:true

        bindRelations(instance)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: "${resourceName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*' {
                response.addHeader(HttpHeaders.LOCATION,
                        g.createLink(
                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: CREATED]
            }
        }
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    @Transactional
    def update() {
        if (!modelCatalogueSecurityService.hasRole(roleForSaveAndEdit)) {
            notAuthorized()
            return
        }
        if(handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }


        bindData instance, getObjectToBind(), [include: includeFields]

        if (instance.hasErrors()) {
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        cleanRelations(instance)

        instance.save flush:true

        bindRelations(instance)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: "${resourceClassName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*'{
                response.addHeader(HttpHeaders.LOCATION,
                        g.createLink(
                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: OK]
            }
        }
    }

    /**
     * Creates a new instance of the resource.  If the request
     * contains a body the body will be parsed and used to
     * initialize the new instance, otherwise request parameters
     * will be used to initialized the new instance.
     *
     * @return The resource instance
     */
    protected T createResource() {
        T instance = resource.newInstance()
        bindData instance, getObjectToBind(), [include: includeFields]
        instance
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
        if (modelCatalogueSecurityService.isUserLoggedIn() && params.format == 'xml' && params.boolean('asset')) {
            Asset asset = renderXMLAsAsset(param)

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

    protected Asset renderXMLAsAsset(object) {
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
                    (object as XML).render(new OutputStreamWriter(out, 'UTF-8'))
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

    protected void notAuthorized() {
        render status: HttpStatus.UNAUTHORIZED
    }

    protected getIncludeFields(){
        GrailsDomainClass clazz = grailsApplication.getDomainClass(resource.name)
        def fields = clazz.persistentProperties.collect{it.name}
        fields.removeAll(['dateCreated','lastUpdated','incomingMappings', 'incomingRelationships', 'modelCatalogueId', 'outgoingMappings', 'outgoingRelationships'])
        fields
    }

    @Override
    protected getObjectToBind(){
        if(request.format=='json') return request.JSON
        request
    }

    /**
     * Clean the relations before persisting.
     * @param instance the instance to persisted
     */
    protected cleanRelations(T instance) { }

    /**
     * Bind the relations as soon as the instance is persisted.
     * @param instance the persisted instance
     */
    protected final bindRelations(T instance) {
        bindRelations(instance, objectToBind)
    }

    protected bindRelations(T instance, Object objectToBind) {}

}
