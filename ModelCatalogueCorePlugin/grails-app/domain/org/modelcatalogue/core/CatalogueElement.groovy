package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ExtensionsWrapper
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

/**
* Catalogue Element - there are a number of catalogue elements that make up the model
 * catalogue (please see DataType, MeasurementUnit, Model, ValueDomain,
* DataElement) they extend catalogue element which allows creation of incoming and outgoing
* relationships between them. They also  share a number of characteristics.
* */
abstract class CatalogueElement implements Extendible {

    def grailsLinkGenerator
    def relationshipService

    String name
    String description
	String modelCatalogueId

    //version number - this gets iterated every time a new version is created from a finalized version
    Integer versionNumber = 1

    //status: once an object is finalized it cannot be changed
    //it's version number is updated and any subsequent update will
    //be mean that the element is superseded. We will provide a supersede function
    //to do this
    ElementStatus status = ElementStatus.DRAFT

    Date versionCreated = new Date()

    // id of the latest version
    Long latestVersionId

    // time stamping
    Date dateCreated
    Date lastUpdated

    //stop null pointers (especially deleting new items)
    Set<Relationship> incomingRelationships = []
    Set<Relationship> outgoingRelationships = []
    Set<Mapping> outgoingMappings = []
    Set<Mapping> incomingMappings = []

    static transients = ['relations', 'info', 'archived', 'incomingRelations', 'outgoingRelations', 'defaultModelCatalogueId', 'ext', 'classifications']

    static hasMany = [incomingRelationships: Relationship, outgoingRelationships: Relationship, outgoingMappings: Mapping,  incomingMappings: Mapping, extensions: ExtensionValue]

    static relationships = [
            incoming: [base: 'isBasedOn', classification: 'classifications', supersession: 'supersedes'],
            outgoing: [base: 'isBaseFor', attachment: 'hasAttachmentOf', supersession: 'supersededBy'],
            bidirectional: [relatedTo: 'relatedTo']
    ]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
		modelCatalogueId nullable: true, unique: true, maxSize: 255, url: true
        dateCreated bindable: false
        lastUpdated bindable: false
        archived bindable: false
        status validator: { val, obj ->
            if (!val) {
                return true
            }
            def oldStatus = null
            if (obj.version != null) {
                oldStatus = obj.getPersistentValue('status')
            }
            if (obj.instanceOf(Model) && oldStatus != ElementStatus.FINALIZED && val == ElementStatus.FINALIZED) {
                if (!checkChildItemsFinalized(obj)) {
                    return ['org.modelcatalogue.core.PublishedElement.status.validator.children']
                }
            }
            return true
        }
        versionNumber bindable: false
        latestVersionId bindable: false, nullable: true
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
		modelCatalogueId boost:10
        name boost:5
        incomingMappings component: true
        extensions component:true
        except = ['versionNumber', 'incomingRelationships', 'outgoingRelationships', 'incomingMappings', 'outgoingMappings']
    }

    static mapping = {
        tablePerHierarchy false
        sort "name"
        description type: "text"
        extensions lazy: false
    }

    static mappedBy = [outgoingRelationships: 'source', incomingRelationships: 'destination', outgoingMappings: 'source', incomingMappings: 'destination']

    /**
     * Functions for specifying relationships between catalogue elements using the
     * org.modelcatalogue.core.Relationship class
     * @return list of items which contains incoming and outgoing relations
     */

    List getRelations() {
        return [
                outgoingRelations,
                incomingRelations
        ].flatten()
    }

    List getIncomingRelations() {
        relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this).items.collect { it.source }
    }

    List getOutgoingRelations() {
        relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this).items.collect { it.destination }
    }

    Long countIncomingRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.INCOMING, refreshed).total
    }

    Long countOutgoingRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, refreshed).total
    }

    Long countRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.BOTH, refreshed).total
    }


    List getIncomingRelationsByType(RelationshipType type) {
        ListWithTotal<Relationship> relationships = relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this, type)
        relationships.items.collect {
            it.source
        }
    }

    List getOutgoingRelationsByType(RelationshipType type) {
        ListWithTotal<Relationship> relationships = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this, type)
        relationships.items.collect {
            it.destination
        }
    }

    List getRelationsByType(RelationshipType type) {
        [getOutgoingRelationsByType(type), getIncomingRelationsByType(type)].flatten()
    }

    int countIncomingRelationsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countByDestinationAndRelationshipType(self, type)
        }
        Relationship.countByDestinationAndRelationshipTypeAndArchived(self, type, false)

    }

    int countOutgoingRelationsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countBySourceAndRelationshipType(self, type)
        }
        Relationship.countBySourceAndRelationshipTypeAndArchived(self, type, false)
    }

    int countRelationsByType(RelationshipType type) {
        countOutgoingRelationsByType(type) + countIncomingRelationsByType(type)
    }


    Relationship createLinkTo(CatalogueElement destination, RelationshipType type) {
        relationshipService.link(this, destination, type)
    }

    Relationship createLinkFrom(CatalogueElement source, RelationshipType type) {
        relationshipService.link(source, this, type)
    }

    Relationship removeLinkTo(CatalogueElement destination, RelationshipType type) {
        relationshipService.unlink(this, destination, type)
    }

    Relationship removeLinkFrom(CatalogueElement source, RelationshipType type) {
        relationshipService.unlink(source, this, type)
    }

    Map<String, Object> getInfo() {
        [
                id: getId(),
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/${getId()}"
        ]
    }

    boolean isArchived() {
        if (!status) return false
        !status.modificable
    }

    def beforeValidate() {
        if (modelCatalogueId) {
            String defaultId = getDefaultModelCatalogueId(true)
            if (defaultId && modelCatalogueId.startsWith(defaultId)) {
                modelCatalogueId = null
            }
        }
    }

    def beforeDelete(){
        new HashSet(outgoingRelationships).each{ Relationship relationship->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        new HashSet(incomingRelationships).each{ Relationship relationship ->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        new HashSet(outgoingMappings).each{ Mapping mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
        new HashSet(incomingMappings).each{ Mapping mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
    }

    void setModelCatalogueId(String mcID) {
        if (mcID != defaultModelCatalogueId) {
            modelCatalogueId = mcID
        }
    }

    Integer countVersions() {
        if (!latestVersionId) {
            return 1
        }
        getClass().countByLatestVersionId(latestVersionId)
    }

    static protected Boolean checkChildItemsFinalized(Model model, Collection<Model> tree = []) {

        if (model.contains.any {
            it.status != ElementStatus.FINALIZED && it.status != ElementStatus.DEPRECATED
        }) return false

        if (!tree.contains(model)) tree.add(model)

        def parentOf = model.parentOf
        if (parentOf) {
            return model.parentOf.any { Model md ->
                if (md.status != ElementStatus.FINALIZED && md.status != ElementStatus.DEPRECATED) return false
                if (!tree.contains(md)) {
                    if (!checkChildItemsFinalized(md, tree)) return false
                }
                return true
            }
        }
        return true
    }

    String getDefaultModelCatalogueId(boolean withoutVersion = false) {
        if (!grailsLinkGenerator) {
            return null
        }
        String resourceName = fixResourceName GrailsNameUtils.getPropertyName(getClass())
        if (withoutVersion) {
            return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/${resourceName}/${latestVersionId ?: id}")
        }
        return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/${resourceName}/${latestVersionId ?: id}.${versionNumber}")
    }

    /**
     * Called before the archived element is persisted to the data store.
     */
    protected void beforeArchive() {}

    static String fixResourceName(String resourceName) {
        if (resourceName.contains('_')) {
            resourceName = resourceName.substring(0, resourceName.indexOf('_'))
        }
        resourceName
    }

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        this.ext.clear()
        this.ext.putAll(ext)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    @Override
    Set<Extension> listExtensions() {
        extensions
    }

    @Override
    Extension addExtension(String name, String value) {
        ExtensionValue newOne = new ExtensionValue(name: name, extensionValue: value, element: this)
        newOne.save()
        assert !newOne.errors.hasErrors()
        addToExtensions(newOne)
        newOne
    }

    @Override
    void removeExtension(Extension extension) {
        if (extension instanceof ExtensionValue) {
            removeFromExtensions(extension)
            extension.delete(flush: true)
        } else {
            throw new IllegalArgumentException("Only instances of ExtensionValue are supported")
        }
    }

    List<Classification> getClassifications() {
        relationshipService.getClassifications(this)
    }

    boolean isReadyForQueries() {
        isAttached() && !hasErrors()
    }

}