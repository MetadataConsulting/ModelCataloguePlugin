package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.path.PathFinder
import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.VerificationPhase
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.*
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import org.modelcatalogue.core.xml.CatalogueXmlPrinter

import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.OK

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
    DataModelGormService dataModelGormService

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
    def mappings(Integer max){
        handleParams(max)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        respond new Mappings(list: Lists.fromCriteria(params, Mapping, "/${resourceName}/${params.id}/mapping") {
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
        addOrRemoveMapping(false)
    }


    /**
     * add mappings that have been assocaited with a particular catalogue element
     * * security checked in the add relation method
     * @param max - number of results
     * @param id, the id of th catalogue element source of the relationship
     *
     */
    def addMapping() {
        addOrRemoveMapping(true)
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
        if(params.status && !(params.status.toLowerCase() in ['finalized', 'deprecated', 'active']) && !modelCatalogueSecurityService.hasRole('VIEWER', getDataModel())) {
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
        T element = queryForResource(params.id)

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
        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
            unauthorized()
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

    /**
     * Merge an element with another element i.e. if two elements have been created for the same item
     * @param id cloned element id
     * @param source, destination elements to be merged
     */
    @Transactional
    def merge() {

        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
            unauthorized()
            return
        }

        if (handleReadOnly()) {
            return
        }

        T source = queryForResource(params.source)
        if (source == null) {
            notFound()
            return
        }

        T destination = queryForResource(params.destination)
        if (destination == null) {
            notFound()
            return
        }

        if (!modelCatalogueSecurityService.hasRole('CURATOR', destination.dataModel) ) {
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
        if (!modelCatalogueSecurityService.hasRole('VIEWER', getDataModel()) ) {
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

        DataModel destinationDataModel = dataModelGormService.findById(params.long('destinationDataModelId'))

        if (!modelCatalogueSecurityService.hasRole('CURATOR', destinationDataModel)) {
            unauthorized()
            return
        }


        if (destinationDataModel == null) {
            notFound()
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
        // TODO: this should be moved to DataModelController
        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
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

        // do not archive relationships as we need to transfer the deprecated elements to the new versions
        instance = elementService.archive(instance, false)

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond instance, [status: OK]
    }

    /**
     * Used on an Archived element to restore it i.e. bring it's status back
     * @param id
     */


    //TODO: this always makes the element finalised - if it's contained in a draft model it should be brought back to a draft state
    @Transactional
    def restore() {

        if (!modelCatalogueSecurityService.hasRole('ADMIN', getDataModel())) {
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

        // do not archive relationships as we need to transfer the deprecated elements to the new versions
        instance = elementService.restore(instance)

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

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
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/changes", auditService.getChanges(params, element))
    }

    /**
     * Return all the history of a element
     * @param id, if of the catalogue element
     * @param max, maximum results
     */
    //TODO: this needs some work - not sure why we need this and the above
    def history(Integer max) {
        CatalogueElement element = queryForResource(params.id)
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
        CatalogueElement element = queryForResource(params.id)
         if(!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/typeHierarchy", elementService.getTypeHierarchy(params, element))
    }

    /**
     * Get the path for a catalogue element - this is used in the ui to open an element in context
     * For example        id a data element appears in two classes this will give you the specific path within one of the classes
     * @param id, id of the catalogue element
     * @param max, maximum results
     */
    def path() {
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        respond new PathFinder().findPath(element)
    }

    /**
     * GENERAL reorder relationships METHOD used for internal and external relationships i.e. if you want one data element contained in a class to come before another
     *security checked in the reorder method
     * @param id, id of the catalogue element
     * @param type,type of relationship
     * @param max, maximum results
     */
    //TODO: this should all go into a service
    private reorderInternal(RelationshipDirection direction, Long id, String type) {
        // begin sanity checks
        //check the user has the minimum role needed
        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
            unauthorized()
            return
        }


        CatalogueElement owner = resource.get(id)
        if (!owner) {
            notFound()
            return
        }

        if (!RelationshipType.readByName(type)) {
            notFound()
            return
        }
        // end sanity checks

        Long movedId = objectToBind?.moved?.id
        Long currentId = objectToBind?.current?.id

        if (!movedId) {
            notAcceptable()
            return
        }

        Relationship rel = Relationship.get(movedId)

        if (!rel) {
            notFound()
            return
        }

        Relationship current = currentId ? Relationship.get(currentId) : null

        if (!current && currentId) {
            notFound()
            return
        }

        if (current && current.relationshipType.versionSpecific && current.source.status != ElementStatus.DRAFT) {
            respond(error: 'You can only reorder items when the element is draft!')
            return
        }


        rel = relationshipService.moveAfter(direction, owner, rel, current)

        respond(id: rel.id, type: rel.relationshipType, ext: OrderedMap.toJsonMap(rel.ext), element: rel.source, relation: rel.destination, direction: 'sourceToDestination', removeLink: RelationshipsMarshaller.getDeleteLink(rel.source, rel), archived: rel.archived, elementType: Relationship.name)
    }

    //TODO: this should all go into a service
    //general remove any relation from a catalogue element based on the relationship type
    // used by the directional remove relation methods
    private void removeRelation(Long id, String type, boolean outgoing) {
        withRetryingTransaction {
            //check the user has the minimum role needed
            if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel())) {
                unauthorized()
                return
            }

            def otherSide = parseOtherSide()

            CatalogueElement source = resource.get(id)
            if (!source) {
                notFound()
                return
            }
            RelationshipType relationshipType = RelationshipType.readByName(otherSide.type ? otherSide.type.name : type)
            if (!relationshipType) {
                notFound()
                return

            }
            Class otherSideType
            try {
                otherSideType = Class.forName (otherSide.relation ? otherSide.relation.elementType : otherSide.elementType)
            } catch (ClassNotFoundException ignored) {
                notFound()
                return
            }

            CatalogueElement destination = otherSideType.get(otherSide.relation ? otherSide.relation.id : otherSide.id )
            if (!destination) {
                notFound()
                return
            }

            def dataModelObject = otherSide.dataModel ?: otherSide.classification
            DataModel dataModel = dataModelObject ? dataModelGormService.findById(dataModelObject.id as Long) : null

            Relationship old = outgoing ?  relationshipService.unlink(source, destination, relationshipType, dataModel) :  relationshipService.unlink(destination, source, relationshipType, dataModel)
            if (!old) {
                notFound()
                return
            }

            if (old.hasErrors()) {
                respond old.errors
                return
            }

            response.status = HttpServletResponse.SC_NO_CONTENT
            render "DELETED"
        }
    }

    //TODO: this should all go into a service
    //general add any relation from a catalogue element based on the relationship type
    // used by the directional add relation methods
    private void addRelation(Long id, String type, boolean outgoing) {

        withRetryingTransaction {
            if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel())) {
                unauthorized()
                return
            }

            def otherSide = parseOtherSide()

            CatalogueElement source = resource.get(id)
            if (!source) {
                notFound()
                return
            }
            RelationshipType relationshipType = RelationshipType.readByName(type)
            if (!relationshipType) {
                notFound()
                return

            }

            def newDataModel = objectToBind['__dataModel'] ?: objectToBind['__classification']
            Long dataModelId = newDataModel instanceof Map ? newDataModel.id as Long : null

            DataModel dataModel = dataModelId ? dataModelGormService.get(dataModelId) : null


            def oldDataModel = objectToBind['__oldDataModel'] ?: objectToBind['__oldClassification']
            Long oldDataModelId = oldDataModel instanceof Map ? oldDataModel.id as Long : null

            DataModel oldDataModelInstance = oldDataModelId ? dataModelGormService.get(oldDataModelId) : null

            if (dataModelId && !dataModel) {
                notFound()
                return
            }

            Class otherSideType
            try {
                otherSideType = Class.forName otherSide.elementType
            } catch (ClassNotFoundException ignored) {
                notFound()
                return
            }

            CatalogueElement destination = otherSideType.get(otherSide.id)
            if (!destination) {
                notFound()
                return
            }

            if (oldDataModelInstance != dataModel) {
                if (outgoing) {
                    relationshipService.unlink(source, destination, relationshipType, oldDataModelInstance)
                } else {
                    relationshipService.unlink(destination, source, relationshipType, oldDataModelInstance)
                }
            }

            RelationshipDefinitionBuilder definition = outgoing ? RelationshipDefinition.create(source, destination, relationshipType) : RelationshipDefinition.create(destination, source, relationshipType)

            definition.withDataModel(dataModel).withMetadata(OrderedMap.fromJsonMap(objectToBind.metadata ?: [:]))

            if (modelCatalogueSecurityService.hasRole('SUPERVISOR', getDataModel())) {
                definition.withIgnoreRules(true)
            }

            Relationship rel = relationshipService.link(definition.definition)

            if (rel.hasErrors()) {
                respond rel.errors
                return
            }

            response.status = HttpServletResponse.SC_CREATED
            RelationshipDirection direction = outgoing ? RelationshipDirection.OUTGOING : RelationshipDirection.INCOMING

            rel.save(flush: true, deepValidate: false, validate: false)

            respond(id: rel.id, type: rel.relationshipType, ext: OrderedMap.toJsonMap(rel.ext), element: CatalogueElementMarshaller.minimalCatalogueElementJSON(direction.getElement(source, rel)), relation: direction.getRelation(source, rel), direction: direction.getDirection(source, rel), removeLink: RelationshipsMarshaller.getDeleteLink(source, rel), archived: rel.archived, elementType: Relationship.name, classification: rel.dataModel, dataModel: rel.dataModel)
        }
    }

    //TODO: not sure what this does
    protected parseOtherSide() {
        request.getJSON()
    }

    /**
     * returns a list of internal relationships
     * @param id, id of the catalogue element
     * @param type,type of relationship
     * @param max, maximum results
     */

    //TODO: this should all go into a service
    private relationshipsInternal(Integer max, String typeParam, RelationshipDirection direction) {
        handleParams(max)

        if (!params.sort) {
            params.sort = direction.sortProperty
        }

        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        RelationshipType type = typeParam ? RelationshipType.readByName(typeParam) : null
        if (typeParam && !type) {
            notFound()
            return
        }

        respond new Relationships(
                type: type,
                owner: element,
                direction: direction,
                list: Lists.fromCriteria(params, "/${resourceName}/${params.id}/${direction.actionName}" + (typeParam ? "/${typeParam}" : ""), direction.composeWhere(element, type, ElementService.getStatusFromParams(params, true), overridableDataModelFilter))
        )
    }


    //TODO: this should all go into a service
    private searchWithinRelationshipsInternal(Integer max, String type, RelationshipDirection direction){
        CatalogueElement element = queryForResource(params?.id)

        if (!element) {
            notFound()
            return
        }

        RelationshipType relationshipType = RelationshipType.readByName(type)

        handleParams(max)

        if (!params.search) {
            respond errors: "No query string to search on"
            return
        }

        ListWithTotalAndType<Relationship> results =  modelCatalogueSearchService.search(element, relationshipType, direction, params)

        respond new Relationships(owner: element,
                direction: direction,
                type: relationshipType,
                list: Lists.wrap(params, "/${resourceName}/${params.id}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${params.search?.encodeAsURL() ?: ''}", results))
    }


    //TODO: this should all go into a service
    private addOrRemoveMapping(boolean add) {
        withRetryingTransaction {
            if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
                unauthorized()
                return
            }

            if (!params.destination || !params.id) {
                notFound()
                return
            }
            CatalogueElement element = queryForResource(params.id)
            if (!element) {
                notFound()
                return
            }

            CatalogueElement destination = queryForResource(params.destination)
            if (!destination) {
                notFound()
                return
            }
            if (add) {
                String mappingString = request.getJSON().mapping
                Mapping mapping = mappingService.map(element, destination, mappingString)
                if (mapping.hasErrors()) {
                    respond mapping.errors
                    return
                }
                response.status = HttpServletResponse.SC_CREATED
                respond mapping
                return
            }
            Mapping old = mappingService.unmap(element, destination)
            if (old) {
                response.status = HttpServletResponse.SC_NO_CONTENT
                render "DELETED"
            } else {
                notFound()
            }
        }
    }

    protected getDefaultSort()  { actionName == 'index' ? 'name'  : null }
    protected getDefaultOrder() { actionName == 'index' ? 'asc'   : null }


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
    private <T> ListWrapper<T> withAdditionalIndexCriteria(ListWrapper<T> list) {
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
            effectiveDataModel = dataModelGormService.findById(dataModelId)
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

}
