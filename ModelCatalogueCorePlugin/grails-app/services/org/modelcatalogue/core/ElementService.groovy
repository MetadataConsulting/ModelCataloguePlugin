package org.modelcatalogue.core

import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.springframework.transaction.TransactionStatus

class ElementService implements Archiver<CatalogueElement> {

    static transactional = true

    def grailsApplication
    def relationshipService
    def modelCatalogueSearchService
    def messageSource

    List<CatalogueElement> list(Map params = [:]) {
        CatalogueElement.findAllByStatus(getStatusFromParams(params), params)
    }

    public <E extends CatalogueElement> List<E> list(params = [:], Class<E> resource) {
        resource.findAllByStatus(getStatusFromParams(params), params)
    }

    Long count(params = [:]) {
        CatalogueElement.countByStatus(getStatusFromParams(params))
    }

    public <E extends CatalogueElement> Long count(params = [:], Class<E> resource) {
        resource.countByStatus(getStatusFromParams(params))
    }

    /**
     * Returns fresh draft version of the element.
     *
     * If the element is already draft the current element is set as deprecated. If the element is finalized it
     * remains untouched.
     *
     * @param element the element from which new draft version should be created.
     * @return fresh draft version of the elements supplied
     */
    public <E extends CatalogueElement> E createDraftVersion(E element) {
        if (!element.latestVersionId) {
            element.latestVersionId = element.id
            element.save(failOnError: true)
        }

        if (element.archived) {
            element.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.cannot.be.archived', 'Cannot create draft version from deprecated element!')
            return element
        }

        GrailsDomainClass domainClass = grailsApplication.getDomainClass(element.class.name) as GrailsDomainClass

        E draft = element.class.newInstance() as E

        for (prop in domainClass.persistentProperties) {
            if (!prop.association) {
                draft.setProperty(prop.name, element[prop.name])
            }
        }

        draft.versionNumber++
        draft.versionCreated = new Date()

        draft.latestVersionId = element.latestVersionId ?: element.id
        draft.status = ElementStatus.UPDATED
        draft.dateCreated = element.dateCreated
        draft.beforeDraftPersisted()

        if (!draft.save()) {
            return draft
        }


        draft.addToSupersedes(element)

        draft = addRelationshipsToDraft(draft, element)
        draft = elementSpecificActions(draft, element)

        if (element.status == ElementStatus.DRAFT) {
            archive(element)
        }

        draft.status = ElementStatus.DRAFT
        (E)draft.save()
    }



     CatalogueElement  archive(CatalogueElement archived) {
        if (archived.archived) {
            return archived
        }

        archived.incomingRelationships.each {
            if (it.relationshipType == RelationshipType.supersessionType) {
                return
            }
            it.archived = true
            it.save(failOnError: true)
        }

        archived.outgoingRelationships.each {
            if (it.relationshipType == RelationshipType.supersessionType) {
                return
            }
            it.archived = true
            it.save(failOnError: true)
        }

        modelCatalogueSearchService.unindex(archived)

        archived.status = ElementStatus.DEPRECATED
        archived.save()
        return archived
    }


    public <E extends CatalogueElement> E finalizeElement(E draft) {
        CatalogueElement.withTransaction { TransactionStatus status ->
            E finalized = draft.publish(this)
            if (finalized.hasErrors()) {
                status.setRollbackOnly()
            }
            finalized
        }
    }

    static ElementStatus getStatusFromParams(params) {
        if (!params.status) {
            return ElementStatus.FINALIZED
        }
        if (params.status instanceof ElementStatus) {
            return params.status
        }
        return ElementStatus.valueOf(params.status.toString().toUpperCase())
    }


    private static <E extends CatalogueElement> E elementSpecificActions(E draft, E element) {
        if(draft instanceof DataElement) {
            if (element.valueDomain) {
                draft.valueDomain = element.valueDomain
            }
        }

        //add all the extensions to the new draft element as well
        draft.ext.putAll element.ext

        draft
    }

    private <E extends CatalogueElement> E addRelationshipsToDraft(E draft, E element) {
        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            relationshipService.link(r.source, draft, r.relationshipType, false)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            relationshipService.link(draft, r.destination, r.relationshipType, false)
        }

        draft
    }


    public <E extends CatalogueElement> E merge(E source, E destination, Set<Classification> classifications = new HashSet(source.classifications)) {
        log.info "Merging $source into $destination"
        if (destination == null) return null

        if (source == null) {
            destination.errors.reject('merge.source.missing', 'Source is missing')
            return destination
        }

        // fallthrough if already merge in progress or the source and destination are the same
        if (source == destination || destination.status == ElementStatus.UPDATED || source.status == ElementStatus.UPDATED) {
            return destination
        }


        // do not merge with already archived ones
        if (destination.archived) {
            destination.errors.reject('merge.destination.already.archived', 'Destination is already archived')
            return destination
        }

        // do not merge with already archived ones
        if (source.archived) {
            if (!(source in destination.supersededBy)) {
                destination.errors.reject('merge.source.already.archived', 'Source is already archived')
            }
            return destination
        }

        if (destination.class != source.class) {
            destination.errors.reject('merge.not.same.type', 'Both source and destination must be of the same type')
            return destination
        }

        ElementStatus originalStatus = destination.status

        GrailsDomainClass grailsDomainClass = grailsApplication.getDomainClass(source.class.name) as GrailsDomainClass

        for (GrailsDomainClassProperty property in grailsDomainClass.persistentProperties) {
            if (property.manyToOne || property.oneToOne) {
                def dstProperty = destination.getProperty(property.name)
                def srcProperty = source.getProperty(property.name)

                if (dstProperty && srcProperty && dstProperty != dstProperty) {
                    destination.errors.rejectValue property.name, 'merge.both.set.' + property.name, "Property '$property.name' is set in both source and destination. Delete it prior the merge."
                    return destination
                }

                destination.setProperty(property.name, dstProperty ?: srcProperty)
            }
        }

        destination.validate()

        if (destination.hasErrors()) {
            return destination
        }

        destination.status = ElementStatus.UPDATED
        destination.save()


        for (Classification classification in new HashSet<Classification>(source.classifications)) {
            classification.removeFromClassifies(source)
            source.removeFromClassifications(classification)
            classification.addToClassifies(destination)
            destination.addToClassifications(classification)
        }

        for (Map.Entry<String, String> extension in new HashMap<String, String>(source.ext)) {
            if(!destination.ext.containsKey(extension.key)) {
                destination.ext.put(extension.key, extension.value)
            }
        }

        for (Relationship rel in new HashSet<Relationship>(source.outgoingRelationships)) {
            if (rel.relationshipType.system) {
                // skip system, currency only supersession
                continue
            }

            if (rel.destination == destination) {
                // do not self-reference
                continue
            }

            if (rel.archived || rel.destination.archived) {
                // no need to transfer archived elements
                continue
            }

            Relationship existing = destination.outgoingRelationships.find { it.destination.name == rel.destination.name && it.relationshipType == rel.relationshipType }

            if (existing) {
                if (rel.destination instanceof CatalogueElement && existing.destination instanceof CatalogueElement && rel.destination.class == existing.destination.class && existing.destination != destination) {
                    if (rel.destination.classifications.intersect(classifications)) {
                        merge rel.destination, existing.destination, classifications
                    }
                }
                continue
            }

            Relationship newOne = relationshipService.link destination, rel.destination, rel.relationshipType, rel.archived
            if (newOne.hasErrors()) {
                destination.errors.rejectValue 'outgoingRelationships', 'unable.to.transfer.relationships', [rel, newOne.errors.allErrors.collect { messageSource.getMessage(it, Locale.default)}.join(", ")] as Object[],  "Unable to transfer relationship {0}. Errors: {1}"

            } else {
                newOne.ext.putAll(rel.ext)
            }
        }

        for (Relationship rel in new HashSet<Relationship>(source.incomingRelationships)) {
            if (rel.relationshipType.system) {
                continue
            }

            if (rel.source == destination) {
                // do not self-reference
                continue
            }

            if (rel.archived || rel.source.archived) {
                // no need to transfer archived elements
                continue
            }

            Relationship existing = destination.incomingRelationships.find { it.source.name == rel.source.name && it.relationshipType == rel.relationshipType }

            if (existing) {
                if (rel.source.class == existing.source.class && existing.source != destination) {
                    if (rel.source.classifications.intersect(classifications)) {
                        merge rel.source, existing.source, classifications
                    }
                }
                continue
            }

            Relationship newOne = relationshipService.link rel.source, destination, rel.relationshipType, rel.archived
            if (newOne.hasErrors()) {
                destination.errors.rejectValue 'incomingRelationships', 'unable.to.transfer.relationships', [rel, newOne.errors.allErrors.collect { messageSource.getMessage(it, Locale.default)}.join(", ")] as Object[],  "Unable to transfer relationship {0}. Errors: {1}"
            } else {
                newOne.ext.putAll(rel.ext)
            }
        }

        if (destination.hasErrors()) {
            return destination
        }

        if (!source.archived) {
            archive source
        }

        destination.addToSupersededBy(source)

        destination.status = originalStatus
        destination.save(failOnError: true)

        log.info "Merged $source into $destination"

        destination
    }


    Map<Long, Long> findModelsToBeInlined() {
        if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST]) {
            // does not work with H2 database
            log.warn "Trying to find inlined models in development mode. This feature does not work with H2 database"
            return [:]
        }
        List<Model> models = Model.executeQuery("""
            select m
            from Model m left join m.incomingRelationships inc
            group by m.name
            having sum(case when inc.relationshipType = :base then 1 else 0 end) = 1
            and sum(case when inc.relationshipType = :hierarchy then 1 else 0 end) = 2
        """, [hierarchy: RelationshipType.hierarchyType, base: RelationshipType.findByName("base")])

        Map<Long, Long> ret = [:]

        for (Model model in models) {
            if (model.ext.from == 'xs:element') {
                ret[model.id] = model.isBasedOn[0].id
            }
        }

        ret
    }

    /**
     * Return models which are very likely to be duplicates.
     * For models having same name as at least one other Model check if they contains same child models and data
     * elements. If so return their id and the set of ids of similar models.
     * @return map with the model id as key and set of ids of duplicate models as value
     */
    Map<Long, Set<Long>> findDuplicateModelsSuggestions() {
        // TODO: create test
        Object[][] results = Model.executeQuery """
            select m.id, m.name, rel.relationshipType.name,  rel.destination.name
            from Model m join m.outgoingRelationships as rel
            where
                m.name in (
                    select model.name from Model model
                    where model.status in :states
                    group by model.name
                    having count(model.id) > 1
                )
            and
                m.status in :states
            and
                rel.archived = false
            and
                (rel.relationshipType = :containment or rel.relationshipType = :hierarchy)
            order by m.name asc, m.dateCreated asc, rel.destination.name asc
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], containment: RelationshipType.findByName('containment'), hierarchy: RelationshipType.findByName('hierarchy')]


        Map<Long, Map<String, Object>> models = new LinkedHashMap<Long, Map<String, Object>>().withDefault { [id: it, elementNames: new TreeSet<String>(), childrenNames: new TreeSet<String>()] }

        for (Object[] row in results) {
            def info = models[row[0] as Long]
            info.name = row[1]
            if (row[2] == 'containment') {
                info.elementNames << row[3].toString()
                info.elementNamesSize = info.elementNames.size()
            } else {
                info.childrenNames << row[3].toString()
                info.childrenNamesSize = info.childrenNames.size()
            }
        }

        Map<Long, Set<Long>> suggestions = new LinkedHashMap<Long, Set<Long>>().withDefault { new TreeSet<Long>() }


        def current = null

        models.each { id, info ->
            if (!current) {
                current = info
            } else {
                if (info.name == current.name && info.elementNamesSize == current.elementNamesSize && info.childrenNamesSize == current.childrenNamesSize && info.elementNames == current.elementNames && info.childrenNames == current.childrenNames) {
                    suggestions[current.id] << info.id
                } else {
                    current = info
                }
            }
        }

        suggestions
    }

    /**
     * Returns data elements which are very likely to be duplicates.
     *
     * Data elements are considered duplicates if they share the exactly same value domain and they are having the same
     * name.
     *
     * @return map with the data element id as key and set of ids of duplicate data elements as value
     */
    Map<Long, Set<Long>> findDuplicateDataElementsSuggestions() {
        // TODO: create test
        Object[][] results = DataElement.executeQuery """
            select count(de.id), de.name, vd.id, vd.name
                from DataElement de join de.valueDomain vd
                where
                    de.status in :states
                group by de.name, vd.id
                having count(de.id) > 1
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED]]

        Map<Long, Set<Long>> elements = new LinkedHashMap<Long, Set<Long>>()

        results.each { row ->
            Long[] duplicates = DataElement.executeQuery """
                    select de.id from DataElement de
                    where de.name = :name
                    and de.valueDomain.id = :vd
                    and de.status in :states

                    order by de.dateCreated
            """, [name: row[1], vd: row[2], states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED]]

            elements[duplicates.head()] = new HashSet<Long>(duplicates.tail().toList())

        }

        elements
    }


}
