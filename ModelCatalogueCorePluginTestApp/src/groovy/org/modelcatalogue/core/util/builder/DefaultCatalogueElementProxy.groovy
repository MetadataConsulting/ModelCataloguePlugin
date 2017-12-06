package org.modelcatalogue.core.util.builder

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.Legacy
import java.lang.reflect.Modifier

@Log4j
class DefaultCatalogueElementProxy<T extends CatalogueElement> implements CatalogueElementProxy<T>, org.modelcatalogue.core.api.CatalogueElement {

    static final List<Class> KNOWN_DOMAIN_CLASSES = [Asset, CatalogueElement, DataModel, DataElement, DataType, EnumeratedType, ReferenceType, MeasurementUnit, DataClass, PrimitiveType, ValidationRule, Tag]

    static final String CHANGE_NEW = "Does Not Exist Yet"
    static final String CHANGE_TYPE = "Type Changed"
    static final String CHANGE_PARAMETERS = "Parameters Changed"
    static final String CHANGE_EXTENSIONS = "Extensions Changed"
    static final String CHANGE_RELATIONSHIP = "Relationship Changed"

    Class<T> domain

    String modelCatalogueId
    String name
    CatalogueElementProxy<DataModel> classification

    boolean newlyCreated
    boolean underControl
    boolean referenceNotPresentInTheCatalogue

    protected CatalogueElementProxyRepository repository

    private final Map<String, Object> parameters = [:]
    private final Map<String, String> extensions = [:]
    final Set<RelationshipProxy> relationships = []
    final Set<String> policies = []

    private CatalogueElementProxy<T> replacedBy
    private T resolved
    private String changed

    DefaultCatalogueElementProxy(CatalogueElementProxyRepository repository, Class<T> domain, String id, CatalogueElementProxy<DataModel> classification, String name, boolean underControl) {
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

    Set<String> getPendingPolicies() {
        policies
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
                resolved = fill(newDomainInstance())
            } catch (InstantiationException ignored) {
                throw new ReferenceNotPresentInTheCatalogueException("Cannot create element from reference $this")
            }


            return resolved
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve $this:\n\n$e", e)
        }

    }

    private T newDomainInstance() {
        if (parameters.containsKey('enumerations')) {
            return new EnumeratedType() as T;
        }
        if (parameters.containsKey('dataClass')) {
            return new ReferenceType() as T;
        }
        if (parameters.containsKey('measurementUnit')) {
            return new PrimitiveType() as T;
        }

        domain.newInstance()
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

        if(key=='domain'){
            domain = value
        }

        if (key=='enumerations') {
            domain = EnumeratedType
        }

        if (key=='dataClass') {
            domain = ReferenceType
        }

        if (key=='measurementUnit') {
            domain = PrimitiveType
        }

        if (key == 'dataModel' || key == 'classification') {
            if (value instanceof CatalogueElementProxy) {
                classification = value as CatalogueElementProxy<DataModel>
            } else {
                throw new IllegalArgumentException("Data model cannot be ${value?.class}. Please, create a proxy before setting the parameter: $value")
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

            if (parameters.containsKey('enumerations') && domain != EnumeratedType) {
                domain = EnumeratedType
                return changed = CHANGE_TYPE
            }

            if (parameters.containsKey('dataClass') && domain != ReferenceType) {
                domain = ReferenceType
                return changed = CHANGE_TYPE
            }

            if (parameters.containsKey('measurementUnit') && domain != PrimitiveType) {
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
                // TODO: check if this can actually happened as the presence check preceeds the relationships check
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
            T result = repository.findByMissingReferenceId(extensions[CatalogueElementProxyRepository.MISSING_REFERENCE_ID])
            if (result) {
                return result
            }
        }

        if (name) {
            if (classification) {
                return repository.tryFind(domain, classification, name, modelCatalogueId)
            }
            if (domain == DataModel && getParameter('semanticVersion')) {
                return repository.tryFindDataModel(name, getParameter('semanticVersion')?.toString(), modelCatalogueId)
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
                return
            }
            element.setProperty(key, value)
        }
        return [:]
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
            classification = relationshipProxy.source
        }
        relationships << relationshipProxy
    }

    @Override
    void addToPendingPolicies(String policyName) {
        policies << policyName
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

        Maps.newLinkedHashMap(typedOther.extensions).each { String key, String value ->
            if (value != null) {
                setExtension(key, value)
            }
        }

        Maps.newLinkedHashMap(typedOther.parameters).each { String key, Object value ->
            if (value != null) {
                setParameter(key, value)
            }
        }

        Sets.newLinkedHashSet(typedOther.relationships).each { RelationshipProxy relationship ->
            // TODO: is really necessary to distinguish between outgoing and incoming
            // can we just copy the relationships over
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

        Sets.newLinkedHashSet(typedOther.policies).each {
            policies << it
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
