package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.*
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Mappings
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.OK

abstract class AbstractCatalogueElementController<T extends CatalogueElement> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xlsx', 'xml']
    static allowedMethods = [outgoing: "GET", incoming: "GET", addIncoming: "POST", addOutgoing: "POST", removeIncoming: "DELETE", removeOutgoing: "DELETE", mappings: "GET", removeMapping: "DELETE", addMapping: "POST"]

    def relationshipService
    def mappingService
    def auditService

	def uuid(String uuid){
        respond resource.findByModelCatalogueId(uuid)
	}

    AbstractCatalogueElementController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractCatalogueElementController(Class<T> resource) {
        super(resource, false)
    }

    def incoming(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }

    def outgoing(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }

    def relationships(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.BOTH)
    }

    def addOutgoing(Long id, String type) {
        addRelation(id, type, true)
    }


    def addIncoming(Long id, String type) {
        addRelation(id, type, false)
    }

    def removeOutgoing(Long id, String type) {
        removeRelation(id, type, true)
    }


    def removeIncoming(Long id, String type) {
        removeRelation(id, type, false)
    }


    def reorderOutgoing(Long id, String type) {
        reorderInternal(RelationshipDirection.OUTGOING, id, type)
    }

    def reorderIncoming(Long id, String type) {
        reorderInternal(RelationshipDirection.INCOMING, id, type)
    }

    def reorderCombined(Long id, String type) {
        reorderInternal(RelationshipDirection.BOTH, id, type)
    }


    private reorderInternal(RelationshipDirection direction, Long id, String type) {
        // begin sanity checks
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
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
            render status: HttpStatus.NOT_ACCEPTABLE
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

        rel = relationshipService.moveAfter(direction, owner, rel, current)

        respond(id: rel.id, type: rel.relationshipType, ext: OrderedMap.toJsonMap(rel.ext), element: rel.source, relation: rel.destination, direction: 'sourceToDestination', removeLink: RelationshipsMarshaller.getDeleteLink(rel.source, rel), archived: rel.archived, elementType: Relationship.name)
    }

    private void removeRelation(Long id, String type, boolean outgoing) {
        withRetryingTransaction {
            if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
                notAuthorized()
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
            DataModel dataModel = dataModelObject ? DataModel.get(dataModelObject.id) : null

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

    private void addRelation(Long id, String type, boolean outgoing) {
        withRetryingTransaction {
            if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
                notAuthorized()
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
            Long dataModelId = newDataModel instanceof Map ? newDataModel.id : null

            DataModel dataModel = dataModelId ? DataModel.get(dataModelId) : null


            def oldDataModel = objectToBind['__oldDataModel'] ?: objectToBind['__oldClassification']
            Long oldDataModelId = oldDataModel instanceof Map ? oldDataModel.id : null

            DataModel oldDataModelInstance = oldDataModelId ? DataModel.get(oldDataModelId) : null

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

            Relationship rel = relationshipService.link(definition.definition)

            if (rel.hasErrors()) {
                respond rel.errors
                return
            }

            response.status = HttpServletResponse.SC_CREATED
            RelationshipDirection direction = outgoing ? RelationshipDirection.OUTGOING : RelationshipDirection.INCOMING

            respond(id: rel.id, type: rel.relationshipType, ext: OrderedMap.toJsonMap(rel.ext), element: CatalogueElementMarshaller.minimalCatalogueElementJSON(direction.getElement(source, rel)), relation: direction.getRelation(source, rel), direction: direction.getDirection(source, rel), removeLink: RelationshipsMarshaller.getDeleteLink(source, rel), archived: rel.archived, elementType: Relationship.name, classification: rel.dataModel, dataModel: rel.dataModel)
        }
    }

    protected parseOtherSide() {
        request.getJSON()
    }

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
                list: Lists.fromCriteria(params, "/${resourceName}/${params.id}/${direction.actionName}" + (typeParam ? "/${typeParam}" : ""), direction.composeWhere(element, type, DataModelFilter.from(modelCatalogueSecurityService.currentUser)))
        )
    }

    def searchIncoming(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }

    def searchOutgoing(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }


    def searchRelationships(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.BOTH)
    }

    private searchWithinRelationshipsInternal(Integer max, String type, RelationshipDirection direction){
        CatalogueElement element = queryForResource(params.id)

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

        respond new Relationships(owner: element, direction: direction, type: relationshipType, list: Lists.wrap(params, "/${resourceName}/${params.id}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${params.search?.encodeAsURL() ?: ''}", results))
    }


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

    def removeMapping() {
        addOrRemoveMapping(false)
    }

    def addMapping() {
        addOrRemoveMapping(true)
    }

    private addOrRemoveMapping(boolean add) {
        withRetryingTransaction {
            if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
                notAuthorized()
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

    def dataModelService

    @Override
    def index(Integer max) {
        handleParams(max)

        if(params.status && !(params.status.toLowerCase() in ['finalized', 'deprecated']) && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }

        if (params.status?.toLowerCase() == 'active') {
            if (modelCatalogueSecurityService.hasRole('CURATOR')){
                respond dataModelService.classified(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                    'in' 'status', [ElementStatus.FINALIZED, ElementStatus.DRAFT]
                }, overridableDataModelFilter)
                return
            }
            respond dataModelService.classified(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }, overridableDataModelFilter)
            return
        }

        if (params.status) {
            respond dataModelService.classified(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params)
            }, overridableDataModelFilter)
            return
        }

        respond dataModelService.classified(Lists.all(params, resource, "/${resourceName}/"), overridableDataModelFilter)
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

        if (!modelCatalogueSecurityService.hasRole('VIEWER') && !(element.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED])) {
            notAuthorized()
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

        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
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

        def newVersion = params.boolean('newVersion',false)
        def semanticVersion = params.semanticVersion

        if (instance.status.ordinal() >= ElementStatus.FINALIZED.ordinal() && !newVersion) {
            instance.errors.rejectValue 'status', 'cannot.modify.finalized.or.deprecated', 'Cannot modify element in finalized or deprecated state!'
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        def ext = params?.ext
        def oldProps = new HashMap(instance.properties)
        oldProps.remove('modelCatalogueId')

        T helper = createResource(oldProps)

        def includeParams = includeFields

        if (!newVersion) newVersion = (request.JSON?.newVersion) ? request.JSON?.newVersion?.toBoolean() : false
        if (!semanticVersion) semanticVersion = request.JSON?.semanticVersion
        if (!ext) ext = OrderedMap.fromJsonMap(request.JSON?.ext)


        if (newVersion) {
            includeParams.remove('status')
            includeParams.remove('semanticVersion')
        }

        bindData(helper, getObjectToBind(), [include: includeParams])


        if (helper.hasErrors()) {
            respond helper.errors, view: 'edit' // STATUS CODE 422
            return
        }

        if (newVersion) {
            ModelCatalogueTypes newType = null
            if (params.newType) {
                newType = ModelCatalogueTypes.getType(params.newType)
            } else if(request.JSON?.newType) {
                newType = ModelCatalogueTypes.getType(request.JSON?.newType)
            }

            // when draft version is created from the UI still just create plain draft ignoring dependencies

            DraftContext context = newType?.implementation ? DraftContext.typeChangingUserFriendly(newType.implementation) : DraftContext.userFriendly()
            if (instance.instanceOf(DataModel)) {
                instance = elementService.createDraftVersion((DataModel) instance, semanticVersion, context) as T
            } else {
                instance = elementService.createDraftVersion(instance, context) as T
            }

            if (instance.hasErrors()) {
                respond instance.errors, view: 'edit' // STATUS CODE 422
                return
            }
        }


        bindData(instance, getObjectToBind(), [include: includeParams])
        instance.save flush:true

        if (ext != null) {
            instance.setExt(ext.collectEntries { key, value -> [key, value?.toString() == "null" ? null : value]})
        }

        bindRelations(instance, newVersion)

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        respond instance, [status: OK]
    }

    @Transactional
    def merge() {

        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
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

        T merged = elementService.merge(source, destination)

        if (merged.hasErrors()) {
            respond merged.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond merged, [status: OK]
    }

    /**
     * Archive element
     * @param id
     */
    @Transactional
    def finalizeElement() {
        // TODO: this should be moved to DataModelController
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
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

        if (!instance.instanceOf(DataModel) && !instance.dataModel) {
            instance.errors.reject 'catalogue.element.at.least.one.data.model', "'$instance.name' has to be declared wihtin a data model"
            respond instance.errors
            return
        }

        if (instance.instanceOf(DataModel)) {
            instance = elementService.finalizeDataModel(instance, params.semanticVersion, params.revisionNotes)
        } else {
            instance = elementService.finalizeElement(instance)
        }

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond instance, [status: OK]
    }

    /**
     * Clone element
     * @param id cloned element id
     * @param destinationDataModelId destination data model id
     */
    @Transactional
    def cloneElement() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
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

        DataModel destinationDataModel = DataModel.get(params.destinationDataModelId)
        if (destinationDataModel == null) {
            notFound()
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
     * Archive element
     * @param id
     */
    @Transactional
    def archive() {
        // TODO: this should be moved to DataModelController
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
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
     * Archive element
     * @param id
     */
    @Transactional
    def restore() {
        // TODO: this should be moved to DataModelController
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            notAuthorized()
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

    def changes(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/changes", auditService.getChanges(params, element))
    }

    def history(Integer max) {
        String name = getResourceName()
        Class type = resource

        if (name in ['primitiveType', 'enumeratedType', 'referenceType']) {
            name = 'dataType'
            type = DataType
        }

        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${name}/${params.id}/history", Lists.lazy(params, type, {
                [type.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, type, "/${name}/${params.id}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }

    // classifications are marshalled with the published element so no need for special method to fetch them
    protected bindRelations(T instance, boolean newVersion, Object objectToBind) {
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
            DataModel dataModel = DraftContext.preferDraft(DataModel.get(domain.id as Long)) as DataModel
            if (!dataModel) {
                log.error "No data model exists for $domain"
                continue
            }
            if (!(dataModel.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                dataModel = elementService.createDraftVersion(dataModel, DraftContext.userFriendly())
            }
            instance.dataModel = dataModel
            instance.save()
        }
    }

    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['extensions', 'versionCreated', 'versionNumber', 'dataModels'])
        fields
    }

    @Override
    protected clearAssociationsBeforeDelete(T instance) {
        // it is safe to remove all classifications
        instance.clearAssociationsBeforeDelete()
    }

    protected DataModelFilter getOverridableDataModelFilter() {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }
}
