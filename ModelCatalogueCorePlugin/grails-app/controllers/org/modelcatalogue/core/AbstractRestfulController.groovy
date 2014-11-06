package org.modelcatalogue.core

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.*

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xlsx']

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
            respond results
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
        respond Lists.all(params, resource, basePath)
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
            respond instance.errors, view: 'create' // STATUS CODE 422
            return
        }

        respond instance
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
            respond instance.errors
            return
        }

        try{
            instance.delete flush:true
        }catch (DataIntegrityViolationException ignored){
            response.status = HttpServletResponse.SC_CONFLICT
            respond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, ignored.message])
            // STATUS CODE 409
            return
        } catch (Exception ignored){
            response.status = HttpServletResponse.SC_NOT_IMPLEMENTED
            respond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, "/${resourceName}/delete/${instance.id}"])
            // STATUS CODE 501
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


        respond instance, [status: CREATED]
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

        respond instance, [status: OK]
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
        respond withLinks(listWrapper)
    }

    @Deprecated
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

    protected void notAuthorized() {
        render status: HttpStatus.UNAUTHORIZED
    }

    protected getIncludeFields(){
        GrailsDomainClass clazz = grailsApplication.getDomainClass(resource.name)
        def fields = clazz.persistentProperties.collect{it.name}
        fields.removeAll(['dateCreated', 'classifiedName', 'lastUpdated','incomingMappings', 'incomingRelationships', 'outgoingMappings', 'outgoingRelationships'])
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

    private Random random = new Random()

    protected <T> T withRetryingTransaction(Map<String, Integer> settings = [:], Closure<T> body) {
        int MAX_ATTEMPTS = settings.attempts ?: 10
        int MIN_BACK_OFF = settings.minBackOff ?: 50
        int INITIAL_BACK_OFF = settings.backOff ?: 200

        int backOff = random.nextInt(INITIAL_BACK_OFF - MIN_BACK_OFF) + MIN_BACK_OFF
        int attempt = 0

        while (attempt < MAX_ATTEMPTS) {
            attempt++
            try {
                return resource.withTransaction(body)
            } catch (ConcurrencyFailureException e) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw new IllegalStateException("Couldn't execute action ${actionName} on ${resource} controller with parameters ${params} after ${MAX_ATTEMPTS} attempts", e)
                }
                log.warn "Exception executing action ${actionName} on ${resource} controller with parameters ${params} (#${attempt} attempt).", e
                Thread.sleep(backOff)
                backOff = 2 * backOff
            }
        }
        throw new IllegalStateException("Couldn't execute action ${actionName} on ${resource} controller with parameters ${params} after ${attempt} attempts")
    }

}
