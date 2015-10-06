package org.modelcatalogue.core

import com.google.common.base.Function
import com.google.common.collect.Lists
import grails.util.GrailsNameUtils
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Published
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.ExtensionsWrapper
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Inheritance
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection

import org.modelcatalogue.core.api.CatalogueElement as ApiCatalogueElement

/**
* Catalogue Element - there are a number of catalogue elements that make up the model
 * catalogue (please see DataType, MeasurementUnit, Model,
* DataElement) they extend catalogue element which allows creation of incoming and outgoing
* relationships between them. They also  share a number of characteristics.
* */
abstract class  CatalogueElement implements Extendible<ExtensionValue>, Published<CatalogueElement>, ApiCatalogueElement {

    def grailsLinkGenerator
    def relationshipService
    def auditService
    def mappingService

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

    static transients = ['relations', 'info', 'archived', 'relations', 'incomingRelations', 'outgoingRelations', 'defaultModelCatalogueId', 'ext', 'classifications', 'combinedVersion', 'inheritedAssociationsNames', 'modelCatalogueResourceName']

    static hasMany = [incomingRelationships: Relationship, outgoingRelationships: Relationship, outgoingMappings: Mapping,  incomingMappings: Mapping, extensions: ExtensionValue]

    static relationships = [
            incoming: [base: 'isBasedOn', declaration: 'declaredWithin', supersession: 'supersedes', favourite: 'isFavouriteOf'],
            outgoing: [base: 'isBaseFor', attachment: 'hasAttachmentOf', supersession: 'supersededBy'],
            bidirectional: [relatedTo: 'relatedTo', synonym: 'isSynonymFor']
    ]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
		modelCatalogueId nullable: true, unique: 'versionNumber', size: 1..255, url: true
        dateCreated bindable: false
        lastUpdated bindable: false
        archived bindable: false
        versionNumber bindable: false
        latestVersionId bindable: false, nullable: true
    }

    static mapping = {
        tablePerHierarchy false
        sort "name"
		name index :'CtlgElement_name_idx'
        description type: "text"
        extensions lazy: false, sort: 'orderIndex'
    }

    static mappedBy = [outgoingRelationships: 'source', incomingRelationships: 'destination', outgoingMappings: 'source', incomingMappings: 'destination']

    /**
     * Functions for specifying relationships between catalogue elements using the
     * org.modelcatalogue.core.Relationship class
     * @return list of items which contains incoming and outgoing relations
     */

    List getRelations() {
        CatalogueElement self = this
        Lists.transform(relationshipService.getRelationships([:], RelationshipDirection.BOTH, this).items, {
            if (it.source == self) return it.destination
            it.source
        } as Function<Relationship, CatalogueElement>)
    }

    List getIncomingRelations() {
        Lists.transform(relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this).items, {
            it.source
        } as Function<Relationship, CatalogueElement>)
    }

    List getOutgoingRelations() {
        Lists.transform(relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this).items, {
            it.destination
        } as Function<Relationship, CatalogueElement>)
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
        getIncomingRelationshipsByType(type).collect {
            it.source
        }
    }

    List getOutgoingRelationsByType(RelationshipType type) {
        getOutgoingRelationshipsByType(type).collect {
            it.destination
        }
    }

    List getRelationsByType(RelationshipType type) {
        [getOutgoingRelationsByType(type), getIncomingRelationsByType(type)].flatten()
    }

    List<Relationship> getIncomingRelationshipsByType(RelationshipType type) {
        relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this, type).items
    }

    List<Relationship> getOutgoingRelationshipsByType(RelationshipType type) {
        relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this, type).items
    }

    List<Relationship> getRelationshipsByType(RelationshipType type) {
        [getOutgoingRelationshipsByType(type), getIncomingRelationshipsByType(type)].flatten()
    }

    int countIncomingRelationshipsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countByDestinationAndRelationshipType(self, type)
        }
        Relationship.countByDestinationAndRelationshipTypeAndArchived(self, type, false)

    }

    int countOutgoingRelationshipsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countBySourceAndRelationshipType(self, type)
        }
        Relationship.countBySourceAndRelationshipTypeAndArchived(self, type, false)
    }

    int countRelationshipsByType(RelationshipType type) {
        countOutgoingRelationshipsByType(type) + countIncomingRelationshipsByType(type)
    }

    Relationship createLinkTo(Map<String, Object> params, CatalogueElement destination, RelationshipType type) {
        relationshipService.link RelationshipDefinition.create(this, destination, type).withParams(params).definition
    }

    Relationship createLinkTo(CatalogueElement destination, RelationshipType type) {
        createLinkTo([:], destination, type)
    }

    Relationship createLinkFrom(Map<String, Object> params, CatalogueElement source, RelationshipType type) {
        relationshipService.link RelationshipDefinition.create(source, this, type).withParams(params).definition
    }

    Relationship createLinkFrom(CatalogueElement source, RelationshipType type) {
        createLinkFrom([:], source, type)
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
        auditService.logElementDeleted(this)
    }

    void setModelCatalogueId(String mcID) {
        if (mcID != defaultModelCatalogueId) {
            modelCatalogueId = mcID
        }
    }

    boolean hasModelCatalogueId() {
        this.@modelCatalogueId != null
    }

    Integer countVersions() {
        if (!latestVersionId) {
            return 1
        }
        getClass().countByLatestVersionId(latestVersionId)
    }


    protected String getModelCatalogueResourceName() {
        fixResourceName GrailsNameUtils.getPropertyName(HibernateProxyHelper.getClassWithoutInitializingProxy(this))
    }

    String getDefaultModelCatalogueId(boolean withoutVersion = false) {
        if (!grailsLinkGenerator) {
            return null
        }
        String resourceName = getModelCatalogueResourceName()
        if (withoutVersion) {
            return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/${resourceName}/${getLatestVersionId() ?: getId()}")
        }
        return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/${resourceName}/${getLatestVersionId() ?: getId()}.${getVersionNumber()}")
    }

    /**
     * Called before the archived element is persisted to the data store.
     */
    void beforeDraftPersisted() {}

    void afterDraftPersisted(CatalogueElement draft) {
        draft.ext.putAll this.ext
        for(Mapping mapping in outgoingMappings) {
            mappingService.map(draft, mapping.destination, mapping.mapping)
        }
        for (Mapping mapping in incomingMappings) {
            mappingService.map(mapping.source, draft, mapping.mapping)
        }
    }

    static String fixResourceName(String resourceName) {
        if (resourceName.contains('_')) {
            resourceName = resourceName.substring(0, resourceName.indexOf('_'))
        }
        resourceName
    }

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        ext = OrderedMap.fromJsonMap(ext)
        for (String key in this.ext.keySet() - ext.keySet()) {
            this.ext.remove key
        }
        this.ext.putAll(ext)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    @Override
    Set<ExtensionValue> listExtensions() {
        extensions
    }

    @Override
    ExtensionValue addExtension(String name, String value) {
        if (getId() && isAttached()) {
            ExtensionValue newOne = new ExtensionValue(name: name, extensionValue: value, element: this)
            FriendlyErrors.failFriendlySaveWithoutFlush(newOne)
            addToExtensions(newOne).save(validate: false)
            auditService.logNewMetadata(newOne)
            Inheritance.withChildren(this) {
                if (!it.ext.containsKey(name)) {
                    it.addExtension(name, value)
                }
            }
            return newOne
        }

        throw new IllegalStateException("Cannot add extension before saving the element (id: ${getId()}, attached: ${isAttached()})")
    }

    @Override
    void removeExtension(ExtensionValue extension) {
        Inheritance.withChildren(this) {
            ExtensionValue oldExt = it.findExtensionByName(extension.name)
            if (oldExt && oldExt.extensionValue == extension.extensionValue) {
                it.removeExtension(oldExt)
            }
        }
        auditService.logMetadataDeleted(extension)
        removeFromExtensions(extension).save(validate: false)
        extension.delete(flush: true)
    }

    @Override
    ExtensionValue findExtensionByName(String name) {
        listExtensions()?.find { it.name == name }
    }

    List<DataModel> getDataModels() {
        relationshipService.getDataModels(this)
    }

    @Deprecated
    List<DataModel> getClassifications() {
        relationshipService.getDataModels(this)
    }

    boolean isReadyForQueries() {
        isAttached() && !hasErrors()
    }

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain.finalize(this).run(publisher)
    }

    @Override
    CatalogueElement createDraftVersion(Publisher<CatalogueElement> publisher, DraftContext strategy) {
        prepareDraftChain(PublishingChain.createDraft(this, strategy)).run(publisher)
    }

    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(dataModels)
    }

    @Override
    boolean isPublished() {
        return status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED]
    }

    /**
     * Method called after successfully finishing the generic merge
     * to finish domain class specific action such as changing the value domain of data elements.
     *
     * @param destination element in which this element was successfully merged
     */
    void afterMerge(CatalogueElement destination) {}

    void afterInsert() {
        auditService.logElementCreated(this)
    }

    void beforeUpdate() {
        auditService.logElementUpdated(this)

        CatalogueElement self = this


        if (inheritedAssociationsNames.any { self.isDirty(it) }) {
            Inheritance.withChildren(this) {
                boolean changed = false

                for (String propertyName in inheritedAssociationsNames) {
                    if (self.isDirty(propertyName) && it.getProperty(propertyName) == self.getPersistentValue(propertyName)) {
                        it.setProperty(propertyName, self.getProperty(propertyName))
                        changed = true
                    }
                }

                if (changed) {
                    FriendlyErrors.failFriendlySaveWithoutFlush(it)
                }
            }

            for (String propertyName in inheritedAssociationsNames) {
                if (self.isDirty(propertyName) && self.getProperty(propertyName) == null){
                    Inheritance.withParents(this) {
                        if (it.getProperty(propertyName) != null) {
                            self.setProperty(propertyName, it.getProperty(propertyName))
                        }
                    }
                }
            }
        }
    }

    void clearAssociationsBeforeDelete() {
        for (DataModel c in this.dataModels) {
            this.removeFromDeclaredWithin(c)
        }

        // it is safe to remove all versioning informations
        for (CatalogueElement e in this.supersededBy) {
            this.removeFromSupersededBy(e)
        }
        for (CatalogueElement e in this.supersedes) {
            this.removeFromSupersedes(e)
        }
        for (User u in this.isFavouriteOf) {
            this.removeFromIsFavouriteOf(u)
        }
    }

    @Override
    int countExtensions() {
        listExtensions()?.size() ?: 0
    }

    ExtensionValue updateExtension(ExtensionValue old, String value) {
        Inheritance.withChildren(this) {
            ExtensionValue oldExt = it.findExtensionByName(old.name)
            if (oldExt && oldExt.extensionValue == old.extensionValue) {
                it.updateExtension(oldExt, value)
            }
        }
        old.orderIndex = System.currentTimeMillis()
        if (old.extensionValue == value) {
            FriendlyErrors.failFriendlySaveWithoutFlush(old)
            return old
        }
        old.extensionValue = value
        if (old.validate()) {
            auditService.logMetadataUpdated(old)
        }
        FriendlyErrors.failFriendlySaveWithoutFlush(old)
    }


    // -- API

    @Override
    List<org.modelcatalogue.core.api.Relationship> getIncomingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        getIncomingRelationshipsByType(type as RelationshipType)
    }

    @Override
    List<org.modelcatalogue.core.api.Relationship> getOutgoingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        getOutgoingRelationshipsByType(type as RelationshipType)
    }

    @Override
    List<org.modelcatalogue.core.api.Relationship> getRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        getRelationshipsByType(type as RelationshipType)
    }

    @Override
    int countIncomingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        countIncomingRelationshipsByType(type as RelationshipType)
    }

    @Override
    int countOutgoingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        countOutgoingRelationshipsByType(type as RelationshipType)
    }

    @Override
    int countRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        countRelationshipsByType(type as RelationshipType)
    }

    @Override
    org.modelcatalogue.core.api.Relationship createLinkTo(Map<String, Object> parameters, org.modelcatalogue.core.api.CatalogueElement destination, org.modelcatalogue.core.api.RelationshipType type) {
        createLinkTo(parameters, destination as CatalogueElement, type as RelationshipType)
    }

    @Override
    org.modelcatalogue.core.api.Relationship createLinkFrom(Map<String, Object> parameters, org.modelcatalogue.core.api.CatalogueElement source, org.modelcatalogue.core.api.RelationshipType type) {
        createLinkFrom(parameters, source as CatalogueElement, type as RelationshipType)
    }

    @Override
    org.modelcatalogue.core.api.Relationship removeLinkTo(org.modelcatalogue.core.api.CatalogueElement destination, org.modelcatalogue.core.api.RelationshipType type) {
        removeLinkTo(destination as CatalogueElement, type as RelationshipType)
    }

    @Override
    org.modelcatalogue.core.api.Relationship removeLinkFrom(org.modelcatalogue.core.api.CatalogueElement source, org.modelcatalogue.core.api.RelationshipType type) {
        removeLinkFrom(source as CatalogueElement, type as RelationshipType)
    }

    String getCombinedVersion() {
        "${getLatestVersionId() ?: getId() ?: '<id not assigned yet>'}.${getVersionNumber()}"
    }

    final void addInheritedAssociations(CatalogueElement child) {
        for (String propertyName in inheritedAssociationsNames) {
            if (child.getProperty(propertyName) == null) {
                child.setProperty(propertyName, getProperty(propertyName))
            }
        }
        FriendlyErrors.failFriendlySave(child)
    }

    final void removeInheritedAssociations(CatalogueElement child) {
        for (String propertyName in inheritedAssociationsNames) {
            if (child.getProperty(propertyName) == getProperty(propertyName)) {
                child.setProperty(propertyName, null)
            }
        }
        FriendlyErrors.failFriendlySave(child)
    }

    List<String> getInheritedAssociationsNames() { Collections.emptyList() }
}