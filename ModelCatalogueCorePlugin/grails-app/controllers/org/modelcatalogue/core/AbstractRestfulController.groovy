package org.modelcatalogue.core

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.StaleStateException
import org.hibernate.util.JDBCExceptionReporter
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.policy.VerificationPhase
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.Errors

import static org.springframework.http.HttpStatus.*

abstract class AbstractRestfulController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xlsx']

    AssetService assetService
    SearchCatalogue modelCatalogueSearchService
    SecurityService modelCatalogueSecurityService
    CatalogueElementService catalogueElementService
    ElementService elementService

    private Random random = new Random()

    AbstractRestfulController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractRestfulController(Class<T> resource) {
        super(resource, false)
    }

    def search(Integer max) {
        handleParams(max)

        if (!params.search) {
            respond errors: "No query string to search on"
            return
        }

        ListWithTotalAndType<T> results = modelCatalogueSearchService.search(resource, params)

        respond Lists.wrap(params, "/${resourceName}/search?search=${URLEncoder.encode(params.search, 'UTF-8')}", results)
    }

    @Override
    def index(Integer max) {
        handleParams(max)
        respond Lists.all(params, resource, basePath)
    }

    def validate() {
        if (handleReadOnly()) {
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

    /**
     * Saves a resource
     */
    @Transactional
    def save() {
        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }
        def instance = createResource()

        instance.validate()

        validatePolicies(VerificationPhase.PROPERTY_CHECK, instance, objectToBind)

        if (instance.hasErrors()) {
            if (!hasUniqueName() || getObjectToBind().size() > 1 || !getObjectToBind().containsKey('name')) {
                respond instance.errors
                return
            }

            Errors errors = instance.errors

            if (errors.getFieldError('name').any { it.code == 'unique' }) {
                T found = resource.findByName(getObjectToBind().name, [sort: 'versionNumber', order: 'desc'])
                if (found) {
                    if (!found.instanceOf(CatalogueElement)) {
                        respond found
                        return
                    }
                    if (found.status != ElementStatus.DRAFT) {
                        found = elementService.createDraftVersion(found, DraftContext.userFriendly())
                    }
                    respond found
                    return
                }
            }

            respond errors
            return
        }

        cleanRelations(instance)

        instance.save flush: true

        bindRelations(instance, false)

        instance.save flush: true

        validatePolicies(VerificationPhase.EXTENSIONS_CHECK, instance, objectToBind)

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        if (favoriteAfterUpdate && modelCatalogueSecurityService.userLoggedIn && instance) {
            modelCatalogueSecurityService.currentUser?.createLinkTo(instance, RelationshipType.favouriteType)
        }


        respond instance, [status: CREATED]
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    @Transactional
    def update() {
        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        bindData instance, getObjectToBind(), [include: includeFields]

        validatePolicies(VerificationPhase.PROPERTY_CHECK, instance, objectToBind)

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        cleanRelations(instance)

        instance.save flush: true

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        bindRelations(instance, false)

        instance.save flush: true

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        validatePolicies(VerificationPhase.EXTENSIONS_CHECK, instance, objectToBind)

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond instance, [status: OK]
    }

    @Override
    def delete() {
        if (!allowDelete()) {
            unauthorized()
            return
        }

        if (handleReadOnly()) {
            return
        }

        def instance = queryForResource(params.id)
        if (!instance) {
            notFound()
            return
        }

        // only CatalogueElements can be deleted now
        if (!(instance instanceof CatalogueElement)) {
            forbidden()
            return
        }

        // find out if CatalogueElement can be deleted
        def manualDeleteRelationships = instance.manualDeleteRelationships(instance instanceof DataModel ? instance : null)
        if (manualDeleteRelationships.size() > 0) {
            log.debug("cannot delete object $instance as there are relationship objects which needs to be handled " +
                          "manually $manualDeleteRelationships")

            def printError
            printError = { Map<CatalogueElement, Object> map ->
                map.collect { key, val ->
                    if (val instanceof Map) {
                        printError(val)
                    } else if (val instanceof DataModel) {
                        [message: "Cannot delete [$key] as it belongs to different data model [$val]. " +
                            "Remove the relationship to this element first."]
                    } else if (val instanceof Relationship) {
                        [message: "Cannot delete [$key] as it is part of relationhip [${val.source}, " +
                            "${val.destination}] which beongs to different data model [${val.dataModel}]"]
                    } else {
                        [message: "Cannot automatically delete [$key], delete it manually or remove the relationship to it."]
                    }
                }.flatten()
            }

            response.status = CONFLICT.value()
            respond errors: printError(manualDeleteRelationships)
            return
        }

        try {
            catalogueElementService.delete(instance)
            noContent()
        } catch (IllegalStateException e) {
            response.status = CONFLICT.value()
            respond errors: message(code: "org.modelcatalogue.core.CatalogueElement.error.delete", args: [instance.name, e.message])
        } catch (e) {
            response.status = INTERNAL_SERVER_ERROR.value()
            respond errors: "Unexpected error while deleting $instance"
            log.error("unexpected error while deleting $instance", e)
        }
    }

    protected handleParams(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 25, 100)
            }
            xml {
                params.max = Math.min(max ?: 10000, 10000)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }
        if (defaultSort && !params.sort) params.sort = defaultSort
        if (defaultOrder && !params.order) params.order = defaultOrder
    }

    protected getBasePath() { "/${resourceName}/" }

    protected getDefaultSort() { null }

    protected getDefaultOrder() { null }

    protected boolean hasUniqueName() { false }

    /**
     * Specify if save and edit are allowed.
     * @return Returns true if user role is CURATOR, false otherwise.
     */
    protected boolean allowSaveAndEdit() {
        modelCatalogueSecurityService.hasRole('CURATOR')
    }

    /**
     * Specify if delete is allowed.
     * @return Returns true if user role is ADMIN, false otherwise.
     */
    protected boolean allowDelete() {
        modelCatalogueSecurityService.hasRole('ADMIN')
    }

    protected boolean isFavoriteAfterUpdate() {
        return false
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
     * @deprecated This method is no longer used by index action
     */
    @Override
    @Deprecated
    protected List<T> listAllResources(Map params) {
        return super.listAllResources(params)
    }

    /**
     * @deprecated This method is no longer used by index action
     */
    @Override
    @Deprecated
    protected Integer countResources() {
        return super.countResources()
    }

    protected getIncludeFields() {
        GrailsDomainClass clazz = grailsApplication.getDomainClass(resource.name)
        def fields = clazz.persistentProperties.collect { it.name }
        fields.removeAll(['dateCreated', 'classifiedName', 'lastUpdated', 'incomingMappings', 'incomingRelationships', 'outgoingMappings', 'outgoingRelationships'])
        fields
    }

    @Override
    protected getObjectToBind() {
        if (request.format == 'json') return request.JSON
        request
    }

    /**
     * Clean the relations before persisting.
     * @param instance the instance to persisted
     */
    protected cleanRelations(T instance) {}

    protected void validatePolicies(VerificationPhase phase, T instance, Object objectToBind) {}

    /**
     * Bind the relations as soon as the instance is persisted.
     * @param instance the persisted instance
     */
    protected final bindRelations(T instance, boolean newVersion) {
        try {
            bindRelations(instance, newVersion, objectToBind)
        } catch (IllegalArgumentException e) {
            instance.errors.reject 'error.binding.relations', e.message
            log.warn "Error binding relations", e
        } catch (Exception e) {
            instance.errors.reject 'error.binding.relations', e.toString()
            log.warn "Error binding relations", e
        }
    }

    protected bindRelations(T instance, boolean newVersion, Object objectToBind) {}

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
            } catch (ConcurrencyFailureException | StaleStateException e) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw new IllegalStateException("Couldn't execute action ${actionName} on ${resource} controller with parameters ${params} after ${MAX_ATTEMPTS} attempts", e as Throwable)
                }
                log.warn "Exception executing action ${actionName} on ${resource} controller with parameters ${params} (#${attempt} attempt).", e as Throwable
                Thread.sleep(backOff)
                backOff = 2 * backOff
            }
        }
        throw new IllegalStateException("Couldn't execute action ${actionName} on ${resource} controller with parameters ${params} after ${attempt} attempts")
    }

    protected void ok() { render status: OK }

    protected void noContent() { render status: NO_CONTENT }

    protected void unauthorized() { render status: UNAUTHORIZED }

    protected void forbidden() { render status: FORBIDDEN }

    @Override
    protected void notFound() { render status: NOT_FOUND }

    protected void methodNotAllowed() { render status: METHOD_NOT_ALLOWED }

    protected void notAcceptable() { render status: NOT_ACCEPTABLE }
}
