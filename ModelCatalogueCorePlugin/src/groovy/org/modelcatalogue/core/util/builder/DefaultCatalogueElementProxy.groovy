package org.modelcatalogue.core.util.builder

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.Legacy

import java.lang.reflect.Modifier

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

@GrailsCompileStatic
@Log4j class DefaultCatalogueElementProxy<T extends CatalogueElement> implements CatalogueElementProxy<T>, org.modelcatalogue.core.api.CatalogueElement {

    static final List<Class> KNOWN_DOMAIN_CLASSES = [Asset, CatalogueElement, DataModel, DataElement, DataType, EnumeratedType, ReferenceType, MeasurementUnit, DataClass, PrimitiveType]

    private static final String CHANGE_NEW = "Does Not Exist Yet"
    private static final String CHANGE_TYPE = "Type Changed"
    private static final String CHANGE_PARAMETERS = "Parameters Changed"
    private static final String CHANGE_EXTENSIONS = "Extensions Changed"
    private static final String CHANGE_RELATIONSHIP = "Relationship Changed"

    Class<T> domain

    String modelCatalogueId
    String name
    String classification

    boolean newlyCreated
    boolean underControl
    boolean referenceNotPresentInTheCatalogue

    protected CatalogueElementProxyRepository repository

    private final Map<String, Object> parameters = [:]
    private final Map<String, String> extensions = [:]
    final Set<RelationshipProxy> relationships = []

    private CatalogueElementProxy<T> replacedBy
    private T resolved
    private String draftRequest
    private String changed

    DefaultCatalogueElementProxy(CatalogueElementProxyRepository repository, Class<T> domain, String id, String classification, String name, boolean underControl) {
        if (!(domain in KNOWN_DOMAIN_CLASSES)) {
            throw new IllegalArgumentException("Only domain classes of $KNOWN_DOMAIN_CLASSES are supported as proxies")
        }

        this.repository = repository
        this.domain = domain

        this.modelCatalogueId = id
        this.name = name
        this.classification = classification

        this.underControl = underControl
    }

    Set<RelationshipProxy> getPendingRelationships() {
        relationships
    }

    boolean isNew() {
        if (replacedBy) {
            return replacedBy.isNew()
        }
        return newlyCreated
    }

    @Override
    final T resolve() {
        try {
            if (replacedBy) {
                return replacedBy.resolve()
            }

            if(resolved) {
                return resolved
            }

            resolved = fill(findExisting())

            if (resolved) {
                return resolved
            }

            try {
                if (Modifier.isAbstract(domain.modifiers)) {
                    throw new InstantiationException("$domain is abstract")
                }

                log.debug "$this not found, creating new one"

                newlyCreated = true
                resolved = fill(domain.newInstance() as T)
            } catch (InstantiationException ignored) {
                throw new ReferenceNotPresentInTheCatalogueException("Cannot create element from reference $this")
            }


            return resolved
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve $this:\n\n$e", e)
        }

    }

    @Override
    Object getParameter(String key) {
        parameters.get(key)
    }

    @Override
    void setParameter(String key, Object value) {
        if (!value) {
            value = null
        }
        if (resolved) {
            throw new IllegalStateException("This catalogue element is already resolved!")
        }

        if (key == 'modelCatalogueId') {
            modelCatalogueId = value?.toString()
        }

        if (key == 'name') {
            name = value?.toString()
        }

        if (key == 'dataModel' || key == 'classification') {
            if (value instanceof String) {
                classification = value
            } else if (value instanceof org.modelcatalogue.core.api.CatalogueElement) {
                classification = value.name
            }
        }

        parameters.put(key, value)
    }

    @Override
    void setExtension(String key, String value) {
        if (resolved) {
            throw new IllegalStateException("This catalogue element is already resolved!")
        }
        extensions.put(key, value)
    }

    void requestDraft() {
        draftRequest = changed == null ? (changed = getChanged()) : changed
    }

    T createDraftIfRequested() {
        if (!draftRequest || referenceNotPresentInTheCatalogue) {
            return null
        }

        T existing = findExisting()

        if (!existing) {
            return null
        }


        if (existing.dataModel && classification && classification != existing.dataModel.name || !existing.dataModel && classification) {
            log.warn "New draft requested for $this but you cannot update element $existing which does not belong to current data model ${classification}. If you need an update, please, declare the element within the dataModel closure first (or update the element outside the data model definition if it does not belong to any data model)."
            return existing
        }

        if (existing.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED] || getEntityClass(existing) != domain) {
            T draft = repository.createDraftVersion(existing, this)
            log.info("New draft version created for $this. Reason: $draftRequest. Draft: $draft")
            return draft
        }
        return existing
    }

    private boolean isParametersChanged(T element) {
        parameters.any { String key, Object value ->
            if (key in [CatalogueElementProxyRepository.AUTOMATIC_NAME_FLAG, CatalogueElementProxyRepository.AUTOMATIC_DESCRIPTION_FLAG]) {
                return false
            }
            if (!element.hasProperty(key)) {
                return false
            }
            def currentValue = element.getProperty(key)
            if (value instanceof CatalogueElementProxy) {
                T realValue = (value as CatalogueElementProxy<T>).findExisting()
                T typedCurrentValue = currentValue as T
                if (realValue?.latestVersionId && realValue?.latestVersionId != typedCurrentValue?.latestVersionId) {
                    log.debug "$this has changed at least one property - property $key (different latest version id)\n\n===NEW===\n$realValue\n===OLD===\n${typedCurrentValue}\n========="
                    return true
                }
                if (realValue?.modelCatalogueId != typedCurrentValue?.modelCatalogueId) {
                    log.debug "$this has changed at least one property - property $key (different id)\n\n===NEW===\n$realValue\n===OLD===\n${typedCurrentValue}\n========="
                    return true
                }
                return false
            }
            if (normalizeWhitespace(currentValue) != normalizeWhitespace(value)) {
                if (key == 'modelCatalogueId' && Legacy.fixModelCatalogueId(value?.toString())?.toString()?.startsWith(element.getDefaultModelCatalogueId(true))) {
                    return false
                }
                if (key == 'name' && parameters[CatalogueElementProxyRepository.AUTOMATIC_NAME_FLAG] && element.name) {
                    // automatic name can't replace one already set
                    return false
                }
                if (key == 'description' && parameters[CatalogueElementProxyRepository.AUTOMATIC_DESCRIPTION_FLAG] && element.description) {
                    // automatic description can't replace one already set
                    return false
                }
                log.debug "$this has changed at least one property - property $key (different value)\n\n===NEW===\n${normalizeWhitespace(value)}\n===OLD===\n${normalizeWhitespace(currentValue)}\n========="
                return true
            }
            return false
        }
    }

    private boolean isExtensionsChanged(T element) {
        extensions.any { String key, String value ->
            boolean result = normalizeWhitespace(element.ext.get(key)) != normalizeWhitespace(value)
            if (result) {
                log.debug "$this has changed at least one extension - extension $key\n\n===NEW===\n${normalizeWhitespace(value)}\n===OLD===\n${normalizeWhitespace(element.ext.get(key))}\n========="
            }
            result
        }
    }

    String getChanged() {
        try {
            T existing = null

            try {
                existing = findExisting()
            } catch (ReferenceNotPresentInTheCatalogueException e) {
                if (!referenceNotPresentInTheCatalogue) {
                    throw e
                }
            }
            if (!existing) {
                return changed = CHANGE_NEW
            }

            if (domain != getEntityClass(existing) && domain != CatalogueElement) {
                return changed = CHANGE_TYPE
            }

            if (domain == DataType && parameters.enumerations) {
                domain = EnumeratedType
                return changed = CHANGE_TYPE
            }

            if (domain == DataType && parameters.dataClass) {
                domain = ReferenceType
                return changed = CHANGE_TYPE
            }

            if (domain == DataType && parameters.measurementUnit) {
                domain = PrimitiveType
                return changed = CHANGE_TYPE
            }

            if (isParametersChanged(existing)) {
                return changed = CHANGE_PARAMETERS
            }

            if (isExtensionsChanged(existing)) {
                return changed = CHANGE_EXTENSIONS
            }

            if (isRelationshipsChanged()) {
                return changed = CHANGE_RELATIONSHIP
            }

            return ""
        } catch (e) {
            throw new IllegalStateException("Error while determining whether $this changed!", e)
        }
    }

    boolean isRelationshipsChanged() {
        Set<Long> foundRelationships = []

        boolean relationshipsChanged = relationships.any {
            RelationshipType type = RelationshipType.readByName(it.relationshipTypeName)

            if (!type) return true

            CatalogueElement source = null

            try {
                source = it.source.findExisting()
            } catch (ReferenceNotPresentInTheCatalogueException e) {
                if (e.isIgnorable(type)) {
                    log.warn("Source ${it.source} cannot be found in the catalogue!", e)
                    return false
                }
                throw e
            }

            if (!source) return true


            CatalogueElement destination = null

            try {
                destination = it.destination.findExisting()
            } catch (ReferenceNotPresentInTheCatalogueException e) {
                if (e.isIgnorable(type)) {
                    log.warn("Destination ${it.destination} cannot be found in the catalogue!", e)
                    return false
                }
                throw e
            }

            if (!destination) return true

            Relationship found = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, type)


            if (!found) {
                log.debug "$this has changed at least one relationship $it"
                return true
            }

            foundRelationships << found.getId()

            if (it.extensions != found.ext) {
                log.debug "$this has changed at least one relationship $it. it has changed metadata. old: ${found.ext}, new: ${it.extensions}"
                return true
            }

            return false
        }

        if (relationshipsChanged) {
            return true
        }

        if (underControl && !repository.isCopyRelationship()) {
            CatalogueElement existing = findExisting()
            if (!existing) {
                return true
            }
            Set<Long> allRelationships = []
            allRelationships.addAll existing.incomingRelationships*.getId()
            allRelationships.addAll existing.outgoingRelationships*.getId()
            return !(allRelationships - foundRelationships).isEmpty()
        }

        return false
    }

    T findExisting() {
        if (modelCatalogueId) {
            T result = repository.findById(domain, modelCatalogueId)
            if (result) {
                return result
            }
            if (!name) {
                markAsReferenceNotPresentInTheCatalogue()
                throw new ReferenceNotPresentInTheCatalogueException("Element not found by ID and there is no name provided to help to find the element: $this")
            }
        }

        if (modelCatalogueId && modelCatalogueId.contains('//localhost')) {
            // these are ignored and not saved to the database, need to store them in the metadata
            // the metadata get saved if not found in a resolved method
            extensions.put CatalogueElementProxyRepository.MISSING_REFERENCE_ID, modelCatalogueId
            return repository.findByMissingReferenceId(extensions[CatalogueElementProxyRepository.MISSING_REFERENCE_ID])
        }

        if (name) {
            if (classification) {
                return repository.tryFind(domain, classification, name, modelCatalogueId)
            }
            return repository.tryFindUnclassified(domain, name, modelCatalogueId)
        }
        throw new IllegalStateException("Missing id, classification and name so there is no way how to find existing element")
    }

    void markAsReferenceNotPresentInTheCatalogue() {
        referenceNotPresentInTheCatalogue = true
        if (replacedBy && replacedBy instanceof DefaultCatalogueElementProxy) {
            replacedBy.referenceNotPresentInTheCatalogue = true
        }
    }

    private T fill(T element) {
        if (!element) {
            return element
        }

        if (changed == null) {
            changed = getChanged()
        }

        if (!changed) {
            return element
        }

        if (changed != CHANGE_RELATIONSHIP) {
            // if there are only changed relationships no need to update

            if (changed != CHANGE_EXTENSIONS) {
                updateProperties(element)
                log.debug "Saving properties of $this"
                repository.save(element)
            }

            updateExtensions(element)
        }

        element
    }

    private Map<String, Object> updateProperties(T element) {
        element.name = name
        if (modelCatalogueId && !Legacy.fixModelCatalogueId(modelCatalogueId).startsWith(element.getDefaultModelCatalogueId(true))) {
            element.modelCatalogueId = Legacy.fixModelCatalogueId(modelCatalogueId)
        }
        parameters.each { String key, Object value ->
            if (key == 'status') return
            if (key == CatalogueElementProxyRepository.AUTOMATIC_NAME_FLAG) return
            if (key == CatalogueElementProxyRepository.AUTOMATIC_DESCRIPTION_FLAG) return
            if (key == 'name' && parameters[CatalogueElementProxyRepository.AUTOMATIC_NAME_FLAG] && element.name) return
            if (key == 'description' && parameters[CatalogueElementProxyRepository.AUTOMATIC_DESCRIPTION_FLAG] && element.description) return
            if (value instanceof CatalogueElementProxy) {
                CatalogueElementProxy typedValue = value as CatalogueElementProxy
                element.setProperty(key, typedValue.resolve())
                return
            }
            if (value instanceof String) {
                element.setProperty(key, value.toString().trim())
            }
            element.setProperty(key, value)
        }
    }

    private <T extends CatalogueElement> void updateExtensions(T element) {
        extensions.each { String key, String value ->
            if (element.ext.get(key) != value) {
                element.ext.put(key, value)
            }
        }
    }

    @Override
    void addToPendingRelationships(RelationshipProxy relationshipProxy) {
        if (!classification && relationshipProxy.relationshipTypeName in [ 'classification', 'declaration' ] && repository.equals(this, relationshipProxy.destination)) {
            classification = relationshipProxy.source.name
        }
        relationships << relationshipProxy
    }

    String toString() {
        "Proxy of $domain.simpleName[id: $modelCatalogueId, dataModel: $classification, name: $name]"
    }

    @Override
    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other) {
        if (!(other instanceof DefaultCatalogueElementProxy) ) {
            throw new IllegalArgumentException("Can only merge with other default catalogue element proxies")
        }

        if (replacedBy && replacedBy == other) {
            return other
        }

        DefaultCatalogueElementProxy<T> typedOther = other as DefaultCatalogueElementProxy<T>

        typedOther.extensions.each { String key, String value ->
            if (value != null) {
                setExtension(key, value)
            }
        }

        typedOther.parameters.each { String key, Object value ->
            if (value != null) {
                setParameter(key, value)
            }
        }

        typedOther.relationships.each { RelationshipProxy relationship ->
            if (repository.equals(this, relationship.source)) {
                RelationshipProxy relationshipProxy = new RelationshipProxy(relationship.relationshipTypeName, this, relationship.destination, relationship.extensions)
                addToPendingRelationships(relationshipProxy)
                relationship.destination.addToPendingRelationships(relationshipProxy)
            }

            if (repository.equals(this, relationship.destination)) {
                RelationshipProxy relationshipProxy = new RelationshipProxy(relationship.relationshipTypeName, relationship.source, this, relationship.extensions)
                addToPendingRelationships(relationshipProxy)
                relationship.source.addToPendingRelationships(relationshipProxy)
            }
        }



        typedOther.replacedBy = this

        if (domain != typedOther.domain) {
            if (domain.isAssignableFrom(typedOther.domain)) {
                domain = typedOther.domain
            }
        }

        this.underControl = this.underControl || typedOther.underControl
        this.referenceNotPresentInTheCatalogue = this.referenceNotPresentInTheCatalogue || typedOther.referenceNotPresentInTheCatalogue

        this
    }

    static normalizeWhitespace(Object o) {
        if (o instanceof CharSequence) {
            return o.toString().replaceAll(/(?m)\s+/, ' ').trim()
        }
        if (o instanceof Enum) {
            return o.toString()
        }
        if (!o) {
            return ''
        }
        return o
    }

    @Override
    String getDescription() {
        return parameters.description?.toString()
    }

    @Override
    void setDescription(String description) {
        parameters.description = description
    }

    @Override
    List<org.modelcatalogue.core.api.Relationship> getIncomingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    List<org.modelcatalogue.core.api.Relationship> getOutgoingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    List<org.modelcatalogue.core.api.Relationship> getRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    int countIncomingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    int countOutgoingRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    int countRelationshipsByType(org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    org.modelcatalogue.core.api.Relationship createLinkTo(Map<String, Object> parameters, org.modelcatalogue.core.api.CatalogueElement destination, org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    org.modelcatalogue.core.api.Relationship createLinkFrom(Map<String, Object> parameters, org.modelcatalogue.core.api.CatalogueElement source, org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    org.modelcatalogue.core.api.Relationship removeLinkTo(org.modelcatalogue.core.api.CatalogueElement destination, org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    org.modelcatalogue.core.api.Relationship removeLinkFrom(org.modelcatalogue.core.api.CatalogueElement source, org.modelcatalogue.core.api.RelationshipType type) {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    Map<String, String> getExt() {
        throw new UnsupportedOperationException("Not Implemented")
    }

    @Override
    ElementStatus getStatus() {
        return parameters.status as ElementStatus
    }

    @Override
    void setStatus(ElementStatus status) {
        parameters.status = status
    }
}
