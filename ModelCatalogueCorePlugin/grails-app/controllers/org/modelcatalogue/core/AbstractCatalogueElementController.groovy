package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.*
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.OK

abstract class AbstractCatalogueElementController<T extends CatalogueElement> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xlsx']
    static allowedMethods = [outgoing: "GET", incoming: "GET", addIncoming: "POST", addOutgoing: "POST", removeIncoming: "DELETE", removeOutgoing: "DELETE", mappings: "GET", removeMapping: "DELETE", addMapping: "POST"]

    def relationshipService
    def mappingService

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

        respond(id: rel.id, type: rel.relationshipType, ext: rel.ext, element: rel.source, relation: rel.destination, direction: 'sourceToDestination', removeLink: RelationshipsMarshaller.getDeleteLink(rel.source, rel), archived: rel.archived, elementType: Relationship.name)
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
            Relationship old = outgoing ?  relationshipService.unlink(source, destination, relationshipType) :  relationshipService.unlink(destination, source, relationshipType)
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

    private addRelation(Long id, String type, boolean outgoing) {
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

            Long classificationId = objectToBind.__classification?.id

            Classification classification = classificationId ? Classification.get(classificationId) : null

            if (classificationId && !classification) {
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
            Relationship rel = outgoing ?  relationshipService.link(source, destination, relationshipType, classification) :  relationshipService.link(destination, source, relationshipType, classification)

            if (rel.hasErrors()) {
                respond rel.errors
                return
            }

            def metadata = objectToBind.metadata

            if (metadata != null) {
                rel.setExt(metadata)
            }

            response.status = HttpServletResponse.SC_CREATED
            RelationshipDirection direction = outgoing ? RelationshipDirection.OUTGOING : RelationshipDirection.INCOMING

            respond(id: rel.id, type: rel.relationshipType, ext: rel.ext, element: CatalogueElementMarshallers.minimalCatalogueElementJSON(direction.getElement(source, rel)), relation: direction.getRelation(source, rel), direction: direction.getDirection(source, rel), removeLink: RelationshipsMarshaller.getDeleteLink(source, rel), archived: rel.archived, elementType: Relationship.name)
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
                list: Lists.fromCriteria(params, "/${resourceName}/${params.id}/${direction.actionName}" + (typeParam ? "/${typeParam}" : ""), direction.composeWhere(element, type, modelCatalogueSecurityService.currentUser?.filteredBy ?: []))
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
        def results =  modelCatalogueSearchService.search(element, relationshipType, direction, params)

        if(results.errors){
            respond results
            return
        }

        def total = (results.total)?results.total.intValue():0

        SimpleListWrapper<Relationship> elements = new SimpleListWrapper<Relationship>(
                base: "/${resourceName}/${params.id}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${params.search?.encodeAsURL() ?: ''}",
                total: total,
                items: results.searchResults,
        )
        respond new Relationships(owner: element, direction: direction, type: relationshipType, list: withLinks(elements))
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

    def relationshipTypeService
    def classificationService

    @Override
    def index(Integer max) {
        handleParams(max)

        respond classificationService.classified(Lists.fromCriteria(params, resource, "/${resourceName}/") {
            eq 'status', ElementService.getStatusFromParams(params)
        })
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
        def ext = params?.ext
        def oldProps = new HashMap(instance.properties)
        oldProps.remove('modelCatalogueId')

        T helper = createResource(oldProps)

        def includeParams = includeFields

        if (!newVersion) newVersion = (request.JSON?.newVersion) ? request.JSON?.newVersion?.toBoolean() : false
        if (!ext) ext = request.JSON?.ext


        if (newVersion) includeParams.remove('status')

        bindData(helper, getObjectToBind(), [include: includeParams])


        if (helper.hasErrors()) {
            respond helper.errors, view: 'edit' // STATUS CODE 422
            return
        }

        if (newVersion) {

            // when draft version is created from the UI still just create plain draft ignoring dependencies
            instance = elementService.createDraftVersion(instance, DraftContext.userFriendly()) as T
            if (instance.hasErrors()) {
                respond instance.errors, view: 'edit' // STATUS CODE 422
                return
            }
        }


        bindData(instance, getObjectToBind(), [include: includeParams])
        instance.save flush:true

        if (ext) {
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

        instance = elementService.finalizeElement(instance)

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
    def archive() {

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

        instance = elementService.archive(instance)

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        respond instance, [status: OK]
    }

    def history(Integer max) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${resourceName}/${params.id}/history", Lists.lazy(params, resource, {
                [resource.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, resource, "/${resourceName}/${params.id}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }

    // classifications are marshalled with the published element so no need for special method to fetch them
    protected bindRelations(T instance, boolean newVersion, Object objectToBind) {
        def classifications = objectToBind.classifications ?: []
        for (classification in instance.classifications.findAll { !(it.id in classifications.collect { it.id as Long } || it.latestVersionId in classifications.collect { it.id as Long })  }) {
            instance.removeFromClassifications classification
            classification.removeFromClassifies instance
        }
        for (domain in classifications) {
            Classification classification = DraftContext.preferDraft(Classification.get(domain.id as Long)) as Classification
            if (!(classification.status in [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING])) {
                classification = elementService.createDraftVersion(classification, DraftContext.userFriendly())
            }
            instance.addToClassifications classification
            classification.addToClassifies instance
        }
    }

    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['extensions', 'versionCreated', 'versionNumber', 'classifications'])
        fields
    }

    @Override
    protected clearAssociationsBeforeDelete(T instance) {
        // it is safe to remove all classifications
        instance.clearAssociationsBeforeDelete()
    }
}
