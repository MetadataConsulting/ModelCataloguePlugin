package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.OK
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.catalogueelement.RemoveRelationService
import org.modelcatalogue.core.events.CatalogueElementArchivedEvent
import org.modelcatalogue.core.events.CatalogueElementRestoredEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotInDraftEvent
import org.modelcatalogue.core.events.CatalogueElementWithErrorsEvent
import org.modelcatalogue.core.events.MappingSavedEvent
import org.modelcatalogue.core.events.MappingWithErrorsEvent
import org.modelcatalogue.core.events.NotFoundEvent
import org.modelcatalogue.core.events.RelationAddedEvent
import org.modelcatalogue.core.events.RelationshipMovedEvent
import org.modelcatalogue.core.events.RelationshipRemovedEvent
import org.modelcatalogue.core.events.RelationshipWithErrorsEvent
import org.modelcatalogue.core.events.RelationshipsEvent
import org.modelcatalogue.core.events.SourceDestinationEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.path.PathFinder
import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.VerificationPhase
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.DestinationClass
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.ParamArgs
import org.modelcatalogue.core.util.SearchParams
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.*
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import org.modelcatalogue.core.events.MetadataResponseEvent
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService

abstract class AbstractCatalogueElementController<T extends CatalogueElement> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [
            outgoing: "GET",
            incoming: "GET",
            addIncoming: "POST",
            addOutgoing: "POST",
            removeIncoming: "DELETE",
            removeOutgoing: "DELETE",
            mappings: "GET",
            removeMapping: "DELETE",
            addMapping: "POST",
            update: "PUT"
    ]

    def relationshipService
    def mappingService
    def auditService
    def dataModelService
    def dataClassService
    RelationshipsInternalService relationshipsInternalService
    AddMappingService addMappingService
    SourceDestinationService sourceDestinationService
    RemoveRelationService removeRelationService

    abstract protected ManageCatalogueElementService getManageCatalogueElementService()

    //used to run reports in background thread
    ExecutorService executorService

    AbstractCatalogueElementController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractCatalogueElementController(Class<T> resource) {
        super(resource, false)
    }


    /**
     * get all incoming relationships for a catalogue element
     * if they do not have the correct access thye are requested to login
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */

    def incoming(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }


    /**
     * get all outgoing relationships or a catalogue element
     * if they do not have the correct access thye are requested to login
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */

    def outgoing(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }


    /**
     * add an outgoing relationship to a catalogue element
     * security checked in the add relation method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */
    def addOutgoing(Long id, String type) {
        addRelation(id, type, true)
    }

    /**
     * add an incoming relationship to a catalogue element
     * security checked in the add relation method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */
    def addIncoming(Long id, String type) {
        addRelation(id, type, false)
    }

    /**
     * remove an outgoing relationship to a catalogue element
     * security checked in the removeRelation method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */
    def removeOutgoing(Long id, String type) {
        removeRelation(id, type, true)
    }

    /**
     * remove an incoming relationship to a catalogue element
     * security checked in the removeRelation method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */

    def removeIncoming(Long id, String type) {
        removeRelation(id, type, false)
    }


    /**
     * reorder relationships i.e. if you want one data element contained in a class to come before another
     * security checked in the reorderInternal  method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */
    def reorderOutgoing(Long id, String type) {
        reorderInternal(RelationshipDirection.OUTGOING, id, type)
    }


    /**
     * reorder relationships i.e. if you want one data element contained in a class to come before another
     * security checked in the reorderInternal  method
     * @param id, catalogue element - source of relationshipsxs you want to
     * @param type, the type of relationship you want to return
     */
    def reorderIncoming(Long id, String type) {
        reorderInternal(RelationshipDirection.INCOMING, id, type)
    }


    /**
     * search incoming relationships i.e. if you want to search the data elements that are contained within a class
     * security checked in the reorderInternal  method
     * @param max - number of results
     * @param type, the type of relationship you want to return
     * @param id, the id of th catalogue element source of the relationship
     */
    def searchIncoming(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }


    /**
     * search outoging relationships i.e. if you want to search the data elements that are contained within a class
     * security checked in the reorderInternal  method
     * @param max - number of results
     * @param type, the type of relationship you want to return
     * @param id, the id of th catalogue element source of the relationship
     */

    def searchOutgoing(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }

    /**
     * return mappings that have been assocaited with a particular catalogue element
     * @param max - number of results
     * @param id, the id of th catalogue element source of the relationship
     */
    def mappings(Integer max) {
        handleParams(max)
        long catalogueElementId = params.long('id')
        CatalogueElement element = findById(catalogueElementId)
        if (!element) {
            notFound()
            return
        }

        respond new Mappings(list: Lists.fromCriteria(params, Mapping, "/${resourceName}/${catalogueElementId}/mapping") {
            eq 'source', element
        })
    }

    /**
     * remove mappings that have been assocaited with a particular catalogue element
     * * security checked in the add relation method
     * @param max - number of results
     * @param id, the id of th catalogue element source of the relationship
     */
    def removeMapping() {

        Long destinationId = params.long('destination')
        Long sourceId = params.long('id')

        MetadataResponseEvent responseEvent = sourceDestinationService.findSourceDestination(sourceId, destinationId)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof SourceDestinationEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }
        SourceDestinationEvent sourceDestinationEvent = responseEvent as SourceDestinationEvent

        Mapping old = mappingService.unmap(sourceDestinationEvent.source, sourceDestinationEvent.destination)
        if ( !old ) {
            notFound()
            return
        }
        response.status = HttpServletResponse.SC_NO_CONTENT
        render "DELETED"
    }


    /**
     * add mappings that have been associated with a particular catalogue element
     * @param id, the id of th catalogue element source of the relationship
     *
     */
    def addMapping() {
        Long destinationId = params.long('destination')
        Long sourceId = params.long('id')

        MetadataResponseEvent responseEvent = sourceDestinationService.findSourceDestination(sourceId, destinationId)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof SourceDestinationEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }
        SourceDestinationEvent sourceDestinationEvent = responseEvent as SourceDestinationEvent

        String mappingString = request.getJSON().mapping

        responseEvent = addMappingService.add(sourceDestinationEvent.source, sourceDestinationEvent.destination, mappingString)

        if ( responseEvent instanceof MappingWithErrorsEvent ) {
            MappingWithErrorsEvent mappingWithErrorsEvent = responseEvent as MappingWithErrorsEvent
            respond mappingWithErrorsEvent.mapping.errors
            return

        }
        if ( !(responseEvent instanceof MappingSavedEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }

        MappingSavedEvent mappingSavedEvent = responseEvent as MappingSavedEvent
        response.status = HttpServletResponse.SC_CREATED
        respond mappingSavedEvent.mapping
    }


    /**
     * Get a list of catalouge elements
     * @param max, maximum number of results
     * check if the the user has a role of viewer - otherwise they can't see draft elements
     * @param status, filter by status
     */

    @Override
    def index(Integer max) {
        handleParams(max)

        //before interceptor deals with this security - this is only applicable to data models and users

        boolean hasRoleViewer = modelCatalogueSecurityService.hasRole('VIEWER', getDataModel())
        if(params.status && !(params.status.toLowerCase() in ['finalized', 'deprecated', 'active']) && !hasRoleViewer) {
            unauthorized()
            return
        }

        ListWithTotalAndType<T> items

        //use elasticsearch for anything that isn't a data model or a user list
        // allows you to filter imports etc quickly and easilt

        items = getAllEffectiveItems(max)


        if (params.boolean('minimal') && items instanceof ListWithTotalAndTypeWrapper) {
            ListWithTotalAndTypeWrapper<T> listWrapper = items as ListWithTotalAndTypeWrapper<T>

            if (listWrapper.list instanceof CustomizableJsonListWithTotalAndType) {
                CustomizableJsonListWithTotalAndType<T> customizable = listWrapper.list as CustomizableJsonListWithTotalAndType<T>
                customizable.customize {
                    it.collect { CatalogueElementMarshaller.minimalCatalogueElementJSON(it) }
                }
            }
        }
        respond items
    }

    /**
     * Shows a single resource
     * @param id The id of the resource
     * @return The rendered resource or a 404 if it doesn't exist
     */
    def show() {
        T element = findById(params.long('id'))

        if (!element) {
            notFound()
            return
        }

        respond element
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    @Transactional
    def update() {
        if(handleReadOnly()) {
            return
        }

        T instance = findById(params.long('id'))
        if (instance == null) {
            notFound()
            return
        }

        DataModel dataModel
        if ( instance instanceof DataModel ) {
            dataModel = instance as DataModel
        } else if ( CatalogueElement.class.isAssignableFrom(instance.class) ) {
            dataModel = instance.dataModel
        }

        if ( dataModel && !dataModelAclService.isAdminOrHasAdministratorPermission(dataModel) ) {
            unauthorized()
            return
        }

        if (!modelCatalogueSecurityService.hasRole('SUPERVISOR') && instance.status.ordinal() >= ElementStatus.FINALIZED.ordinal()) {
            instance.errors.rejectValue 'status', 'cannot.modify.finalized.or.deprecated', 'Cannot modify element in finalized or deprecated state!'
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        def ext = params?.ext
        def oldProps = new HashMap(instance.properties)
        oldProps.remove('modelCatalogueId')

        T helper = createResource(oldProps)

        def includeParams = includeFields

        if (!ext) ext = OrderedMap.fromJsonMap(request.JSON?.ext)


        bindData(helper, getObjectToBind(), [include: includeParams])

        helper.id = params.long('id')

        validatePolicies(VerificationPhase.PROPERTY_CHECK, helper, getObjectToBind())

        if (helper.hasErrors()) {
            respond helper.errors, view: 'edit' // STATUS CODE 422
            return
        }

        ModelCatalogueTypes newType = null
        if (params.newType) {
            newType = ModelCatalogueTypes.values().find { it.toString() == params.newType }
        } else if(request.JSON?.newType) {
            newType = ModelCatalogueTypes.values().find { it.toString() == request.JSON?.newType }
        }

        if (newType && newType.implementation) {
            instance = elementService.changeType(instance, newType.implementation) as T
        }

        bindData(instance, getObjectToBind(), [include: includeParams])
        instance.save flush:true

        if (ext != null) {
            instance.setExt(ext)
        }

        bindRelations(instance, false)

        instance.save flush:true
        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        validatePolicies(VerificationPhase.EXTENSIONS_CHECK, instance, getObjectToBind())

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        if (favoriteAfterUpdate && modelCatalogueSecurityService.userLoggedIn) {
            modelCatalogueSecurityService.currentUser?.createLinkTo(instance, RelationshipType.favouriteType)
        }

        instance.save flush:true

        respond instance, [status: OK]
    }

    protected boolean hasAuthorityOrHasAdministrationPermission(String authority, Object instance) {

        DataModel dataModel
        if ( instance instanceof DataModel ) {
            dataModel = instance as DataModel
        } else if ( CatalogueElement.class.isAssignableFrom(instance.class) ) {
            dataModel = instance.dataModel
        }

        boolean isCuratorOrAdminOrSupervisor = SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.roles(authority))
        dataModel && ( isCuratorOrAdminOrSupervisor || dataModelAclService.hasAdministratorPermission(dataModel))
    }

    /**
     * Merge an element with another element i.e. if two elements have been created for the same item
     * @param id cloned element id
     * @param source, destination elements to be merged
     */
    @Transactional
    def merge() {

        if (handleReadOnly()) {
            return
        }

        T source = findById(params.source)
        if (source == null) {
            notFound()
            return
        }

        if ( !hasAuthorityOrHasAdministrationPermission('CURATOR', source) ) {
            unauthorized()
            return
        }

        Long destinationId = params.long('destination')
        T destination = findById(destinationId)
        if (destination == null) {
            notFound()
            return
        }
        if ( !hasAuthorityOrHasAdministrationPermission('CURATOR', destination) ) {
            unauthorized()
            return
        }

        T merged = elementService.merge(source, destination)

        if (merged.hasErrors()) {
            respond merged.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond merged, [status: OK]
    }


    /**
     * Clone element
     * @param id cloned element id
     * @param destinationDataModelId destination data model id
     */
    @Transactional
    def cloneElement() {
        if (handleReadOnly()) {
            return
        }

        T instance = findById(params.long('id'))
        if (instance == null) {
            notFound()
            return
        }
        if ( CatalogueElement.class.isAssignableFrom(instance.class) )  {
            DataModel dataModel = instance.dataModel
            if ( !dataModelAclService.isAdminOrHasReadPermission(dataModel) ) {
                unauthorized()
                return
            }
        }

        long destinationDataModelId = params.long('destinationDataModelId')
        DataModel destinationDataModel = dataModelGormService.findById(destinationDataModelId)
        if (destinationDataModel == null) {
            notFound()
            return
        }
        if ( !dataModelAclService.isAdminOrHasAdministratorPermission(destinationDataModel) ) {
            unauthorized()
            return
        }

        if (destinationDataModel.status != ElementStatus.DRAFT) {
            instance.errors.reject 'catalogue.element.can.only.clone.to.draft.data.models', "Cannot clone to non-draft data model $destinationDataModel.name"
            respond instance.errors
            return
        }

        if (!instance.instanceOf(DataModel) && !instance.dataModel) {
            instance.errors.reject 'catalogue.element.at.least.one.data.model', "'$instance.name' has to be declared wihtin a data model to be cloned"
            respond instance.errors
            return
        }


        DataModel sourceDataModel = instance.instanceOf(DataModel) ? instance as DataModel : instance.dataModel
        T clone = elementService.cloneElement(instance, CloningContext.create(sourceDataModel, destinationDataModel))

        if (clone.hasErrors()) {
            respond clone.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond clone, [status: OK]
    }

    /**
     * Used to Archive an element
     * @param id
     */
    @Transactional
    def archive() {
        if (handleReadOnly()) {
            return
        }

        Long catalogueElementId = params.long('id')
        MetadataResponseEvent responseEvent = manageCatalogueElementService.archive(catalogueElementId)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }
        if (!responseEvent instanceof CatalogueElementArchivedEvent) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }

        T instance = (responseEvent as CatalogueElementArchivedEvent).catalogueElement
        respond instance, [status: OK]
    }

    /**
     * Used on an Archived element to restore it i.e. bring it's status back
     * @param id
     */
    //TODO: this always makes the element finalised - if it's contained in a draft model it should be brought back to a draft state
    @Transactional
    def restore() {

        Long catalogueElementId = params.long('id')
        MetadataResponseEvent responseEvent = manageCatalogueElementService.restore(catalogueElementId)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }
        if (!responseEvent instanceof CatalogueElementRestoredEvent) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }
        T instance = (responseEvent as CatalogueElementRestoredEvent).catalogueElement

        respond instance, [status: OK]
    }

    /**
     * Return all the changes assocaited with a catalouge element
     * @param id, if of the catalogue element
     * @param max, maximum results
     */
    //TODO: this needs some work - not sure why we need this and the below
    def changes(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = findById(params.long('id'))
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.long('id')}/changes", auditService.getChanges(params, element))
    }

    /**
     * Return all the history of a element
     * @param id, if of the catalogue element
     * @param max, maximum results
     */
    //TODO: this needs some work - not sure why we need this and the above
    def history(Integer max) {
        CatalogueElement element = findById(params.long('id'))
        if (!element) {
            notFound()
            return
        }

        params.max = Math.min(max ?: 10, 100)
        respond Lists.wrap(params,  "/${resourceName}/${params.id}/history", auditService.getElementChanges(params, element.latestVersionId ?: element.id))
    }

    /**
     * Return all the elements that this inherits ?
     * @param id,        id of the catalogue element
     * @param max, maximum results
     */
    def typeHierarchy(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        Long id = params.long('id')
        CatalogueElement element = findById(id)
        if ( !element ) {
            notFound()
            return
        }
        String base = "/${resourceName}/${id}/typeHierarchy" as String
        respond Lists.wrap(params, base, elementService.getTypeHierarchy(params, element))
    }

    /**
     * Get the path for a catalogue element - this is used in the ui to open an element in context
     * For example        id a data element appears in two classes this will give you the specific path within one of the classes
     * @param id, id of the catalogue element
     * @param max, maximum results
     */
    def path() {
        CatalogueElement element = findById(params.long('id'))
        if (!element) {
            notFound()
            return
        }

        respond new PathFinder().findPath(element)
    }

    /**
     * GENERAL reorder relationships METHOD used for internal and external relationships i.e. if you want one data element contained in a class to come before another
     * @param id, id of the catalogue element
     * @param type,type of relationship
     * @param max, maximum results
     */
    private def reorderInternal(RelationshipDirection direction, Long catalogueElementId, String type) {
        Long movedId = objectToBind?.moved?.id as Long
        if (!movedId) {
            notAcceptable()
            return
        }
        Long currentId = objectToBind?.current?.id as Long

        MetadataResponseEvent responseEvent = manageCatalogueElementService.reorderInternal(direction, catalogueElementId, type, movedId, currentId)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }
        if (responseEvent instanceof CatalogueElementStatusNotInDraftEvent) {
            respond(error: 'You can only reorder items when the element is draft!')
            return
        }
        if (!(responseEvent instanceof RelationshipMovedEvent)) {
            log.warn "got an unexpected response event {responsEvent.class.name} in reorderInternal"
            return
        }

        Relationship rel = (RelationshipMovedEvent as RelationshipMovedEvent).relationship
        respond(id: rel.id,
                type: rel.relationshipType,
                ext: OrderedMap.toJsonMap(rel.ext),
                element: rel.source, relation:
                rel.destination,
                direction: 'sourceToDestination',
                removeLink: RelationshipsMarshaller.getDeleteLink(rel.source, rel),
                archived: rel.archived,
                elementType: Relationship.name)
    }

    //TODO: this should all go into a service
    //general remove any relation from a catalogue element based on the relationship type
    // used by the directional remove relation methods
    private void removeRelation(Long id, String type, boolean outgoing) {
        Object otherSide = parseOtherSide()
        DestinationClass destinationClass = new DestinationClass(
            className: otherSide.relation ? otherSide.relation.elementType : otherSide.elementType,
            id: otherSide.relation ? otherSide.relation.id : otherSide.id
        )
        String relationshipTypeName = otherSide.type ? otherSide.type.name : type
        def dataModelObject = otherSide.dataModel ?: otherSide.classification
        Long dataModelId = dataModelObject?.id as Long

        MetadataResponseEvent responseEvent = sourceDestinationService.findSourceDestination(id, destinationClass)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof SourceDestinationEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }
        SourceDestinationEvent sourceDestinationEvent = responseEvent as SourceDestinationEvent

        CatalogueElement source = sourceDestinationEvent.source
        CatalogueElement destination = sourceDestinationEvent.destination
        responseEvent = removeRelationService.removeRelation(relationshipTypeName,
                                             dataModelId,
                                             outgoing,
                                             source,
                                             destination)
        handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof RelationshipRemovedEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }

        response.status = HttpServletResponse.SC_NO_CONTENT
        render "DELETED"
    }

    @CompileDynamic
    protected DestinationClass destinationClassFromJsonPayload()  {
        DestinationClass destination = new DestinationClass()
        Object otherSide = parseOtherSide()
        destination.className = otherSide.elementType as String
        destination.id = otherSide.id as Long
        destination
    }


   /**
    * general add any relation from a catalogue element based on the relationship type used by the directional add relation methods
    */
//    @CompileStatic
    protected def addRelation(Long catalogueElementId, String type, boolean outgoing) {

        try {
            def otherSide = parseOtherSide()
            DestinationClass destinationClass = destinationClassFromJsonPayload()
            def objectToBindParam = getObjectToBind()
            MetadataResponseEvent metadataResponse = manageCatalogueElementService.addRelation(catalogueElementId,
                    type,
                    outgoing as Boolean,
                    objectToBindParam as Object,
                    destinationClass)

            boolean handled = handleMetadataResponseEvent(metadataResponse)
            if ( handled ) {
                return
            }

            if( !(metadataResponse instanceof RelationAddedEvent)) {
                notFound()
                return
            }
            RelationAddedEvent relationAddedEvent = metadataResponse as RelationAddedEvent
            response.status = HttpServletResponse.SC_CREATED
            respond(id: relationAddedEvent.rel.id,
                    type: relationAddedEvent.rel.relationshipType,
                    ext: OrderedMap.toJsonMap(relationAddedEvent.rel.ext),
                    element: CatalogueElementMarshaller.minimalCatalogueElementJSON(relationAddedEvent.direction.getElement(relationAddedEvent.source, relationAddedEvent.rel)),
                    relation: relationAddedEvent.direction.getRelation(relationAddedEvent.source, relationAddedEvent.rel),
                    direction: relationAddedEvent.direction.getDirection(relationAddedEvent.source, relationAddedEvent.rel),
                    removeLink: RelationshipsMarshaller.getDeleteLink(relationAddedEvent.source, relationAddedEvent.rel),
                    archived: relationAddedEvent.rel.archived, elementType: Relationship.name,
                    classification: relationAddedEvent.rel.dataModel,
                    dataModel: relationAddedEvent.rel.dataModel)
        } catch (ClassNotFoundException ignored) {
            notFound()
        }
    }

    //TODO: not sure what this does
    protected Object parseOtherSide() {
        request.getJSON()
    }

    /**
     * returns a list of internal relationships
     * @param id, id of the catalogue element
     * @param type,type of relationship
     * @param max, maximum results
     */
    private relationshipsInternal(Integer max, String typeParam, RelationshipDirection direction) {
        ParamArgs paramArgs = instantiateParamArgs(max)
        if (!params.sort) {
            paramArgs.sort = direction.sortProperty
        }

        Long catalogueElementId = params.long('id')
        MetadataResponseEvent responseEvent = relationshipsInternalService.relationshipsInternal(catalogueElementId,
                typeParam,
                direction,
                resourceName,
                paramArgs,
                overridableDataModelFilter)

        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof RelationshipsEvent) ) {
            log.warn "got an unexpected response event {responsEvent.class.name} in relationshipsInternal"
            return
        }

        RelationshipsEvent relationshipsEvent = responseEvent as RelationshipsEvent
        Relationships relationships = relationshipsEvent.relationships
        respond relationships
    }

    protected void searchWithinRelationshipsInternal(Integer max, String type, RelationshipDirection direction){
        String search = params.search
        if (!search) {
            respond errors: "No query string to search on"
            return
        }
        Long catalogueElementId = params.long('id')
        ParamArgs paramArgs = instantiateParamArgs(max)
        SearchParams searchParams = SearchParams.of(params, paramArgs)
        MetadataResponseEvent responseEvent = manageCatalogueElementService.searchWithinRelationships(catalogueElementId,
                type,
                direction,
                searchParams)

        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }

        if ( !(responseEvent instanceof RelationshipsEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }
        RelationshipsEvent relationshipsEvent = responseEvent as RelationshipsEvent
        respond relationshipsEvent.relationships
    }

    protected String getDefaultSort()  { actionName == 'index' ? 'name'  : null }
    protected String getDefaultOrder() { actionName == 'index' ? 'asc'   : null }


    /**
     * returns all effective items for a user as a list of data models based on the specific data model role
     * i.e. we can view the basic info of data models that we aren't subscribed to
     * @param id of catalogue element - if there is one
     */
    protected ListWrapper<T> getAllEffectiveItems(Integer max) {

        if (params.status?.toLowerCase() == 'active') {
            if (modelCatalogueSecurityService.hasRole('VIEWER', getDataModel())){
                return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                    'in' 'status', [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
                }), overridableDataModelFilter)
            }
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }), overridableDataModelFilter)
        }

        if (params.status) {
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER', getDataModel()))
            }), overridableDataModelFilter)
        }

        return dataModelService.classified(withAdditionalIndexCriteria(Lists.all(params, resource, "/${resourceName}/")), overridableDataModelFilter)
    }

    //TODO: not sure what this does
    protected <T> ListWrapper<T> withAdditionalIndexCriteria(ListWrapper<T> list) {
        if (!hasAdditionalIndexCriteria()) {
            return list
        }

        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot add additional criteria list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }
        if (!(list.list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot add additional criteria list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        list.list.criteria.with buildAdditionalIndexCriteria()

        return list
    }

    //TODO: not sure what this does
    protected boolean hasAdditionalIndexCriteria() {
        return false
    }

    //TODO: not sure what this does
    protected Closure buildAdditionalIndexCriteria() {
        return {}
    }

    //TODO: not sure what this does - remove
    protected String getHistoryOrderDirection() {
        'desc'
    }

    //TODO: not sure what this does - remove
    protected String getHistorySortProperty() {
        'versionNumber'
    }

    //TODO: not sure what this does
    // classifications are marshalled with the published element so no need for special method to fetch them
    protected bindRelations(T instance, boolean newVersion, Object objectToBind) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        if (!instance.readyForQueries) {
            return
        }
        def dataModels = objectToBind.classifications ?: objectToBind.dataModels ?: []
        if (!dataModels && !instance.dataModel && !(instance.instanceOf(DataModel))) {
            instance.errors.reject 'catalogue.element.at.least.one.data.model', "'$instance.name' has to be declared wihtin a data model"
        }
        if (dataModels?.size() > 1) {
            instance.errors.reject 'catalogue.element.no.more.than.one.data.model', "'$instance.name' has to be declared wihtin a single data model"
            return
        }
        for (domain in dataModels) {
            DataModel dataModel = DraftContext.userFriendly().findExisting(dataModelGormService.findById(domain.id as Long)) as DataModel
            if (!dataModel) {
                log.error "No data model exists for $domain"
                continue
            }
            if (!(dataModel.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                instance.errors.reject 'catalogue.element.data.model.must.be.draft', "'$instance.name' has to be declared within draft data model."
                return
            }
            instance.dataModel = dataModel
            instance.save()
        }
    }

    /**
     * not sure what this does - think it collects the fields used in the json - removes the ones we don't want?
     * @param id of data model
     */
    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['extensions', 'versionCreated', 'versionNumber', 'dataModels'])
        fields
    }


    //TODO: not sure what this does
    protected void validatePolicies(VerificationPhase phase, T instance, Object objectToBind) {
        DataModel effectiveDataModel = instance.dataModel
        def dataModels = objectToBind.classifications ?: objectToBind.dataModels
        if (dataModels) {
            Long dataModelId = dataModels.first().id as Long
            if ( dataModelId != null ) {
                effectiveDataModel = dataModelGormService.findById(dataModelId)
            }
        }

        if (!effectiveDataModel && resource == DataModel) {
            effectiveDataModel = instance as DataModel
        }


        if (effectiveDataModel && effectiveDataModel.policies) {
            for (DataModelPolicy policyEntity in effectiveDataModel.policies) {
                Policy policy = policyEntity.policy
                policy.verifySingle(phase, effectiveDataModel, instance)
            }
        }
    }

    //TODO: not sure what this does
    protected DataModelFilter getOverridableDataModelFilter() {
        if (params.dataModel) {
            DataModel dataModel = dataModelGormService.findById(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }

    /**
     * @return true if the event was handled, false if it was not
     */
    protected boolean handleMetadataResponseEvent(MetadataResponseEvent responseEvent) {
        if (responseEvent instanceof NotFoundEvent) {
            notFound()
            return true
        }
        if (responseEvent instanceof UnauthorizedEvent) {
            unauthorized()
            return true
        }
        if (responseEvent instanceof CatalogueElementWithErrorsEvent) {
            respond responseEvent.catalogueElement.errors, view: 'edit' // STATUS CODE 422
            return true
        }
        if ( responseEvent instanceof RelationshipWithErrorsEvent ) {
            RelationshipWithErrorsEvent relationshipWithErrorsEvent = responseEvent as RelationshipWithErrorsEvent
            respond relationshipWithErrorsEvent.relationship.errors
            return true
        }

        false
    }
}
