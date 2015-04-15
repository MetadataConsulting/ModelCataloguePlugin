package org.modelcatalogue.core

import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.transaction.TransactionStatus

class ElementService implements Publisher<CatalogueElement> {

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


    public <E extends CatalogueElement> E createDraftVersion(E element, DraftContext context, boolean skipRelationships = false) {
        Closure<E> code = { TransactionStatus status = null ->
            E draft = element.createDraftVersion(this, context) as E
            if (draft.hasErrors()) {
                status?.setRollbackOnly()
                return element
            }
            context.classifyDrafts()
            if (!skipRelationships) {
                context.resolvePendingRelationships()
            }
            return draft
        }
        if (skipRelationships) {
            return code()
        } else {
            return CatalogueElement.withTransaction(code)
        }
    }



    CatalogueElement archive(CatalogueElement archived) {
        if (archived.archived) {
            return archived
        }

        archived.incomingRelationships.each {
            if (it.relationshipType == RelationshipType.supersessionType) {
                return
            }
            it.archived = true
            FriendlyErrors.failFriendlySave(it)
        }

        archived.outgoingRelationships.each {
            if (it.relationshipType == RelationshipType.supersessionType) {
                return
            }
            it.archived = true
            FriendlyErrors.failFriendlySave(it)
        }

        modelCatalogueSearchService.unindex(archived)

        archived.status = ElementStatus.DEPRECATED
        archived.save(validate: false)
        return archived
    }


    public <E extends CatalogueElement> E finalizeElement(E draft) {
        CatalogueElement.withTransaction { TransactionStatus status ->
            E finalized = draft.publish(this) as E
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
        if (destination.status != ElementStatus.DRAFT) {
            destination.errors.reject('merge.destination.no.draft', 'Destination is not draft')
            return destination
        }

        // do not merge with already archived ones
        if (source.status != ElementStatus.DRAFT) {
            destination.errors.reject('merge.source.no.draft', 'Source is not draft')
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
        destination.save(flush: true, validate: false)


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
                // skip system, currently only supersession
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

        destination.addToSupersededBy(source, skipUniqueChecking: true)

        destination.status = originalStatus

        FriendlyErrors.failFriendlySave(destination)

        source.afterMerge(destination)

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
        """, [hierarchy: RelationshipType.hierarchyType, base: RelationshipType.readByName("base")])

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
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED], containment: RelationshipType.readByName('containment'), hierarchy: RelationshipType.readByName('hierarchy')]


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
     * Return enumerated types which are very likely to be duplicates.
     * Enums are very likely duplicates if they have similar enum values.
     * @return map with the enum id as key and set of ids of duplicate enums as value
     */
    Map<Long, Set<Long>> findDuplicateEnumerationsSuggestions() {
        Object[][] results = EnumeratedType.executeQuery """
            select e.id, e.enumAsString
            from EnumeratedType e
            where e.status in :states
            order by e.name
        """, [states: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.FINALIZED]]


        Map<String, Set<Long>> enums = [:].withDefault { [] as TreeSet<Long> }

        for (Object[] row in results) {
            enums[getNormalizedEnumValues(EnumeratedType.stringToMap(row[1]))] << row[0]
        }

        enums.findAll { String key, Set<Long> values -> values.size() > 1 }.collectEntries { String key, Set<Long> values ->
            def valuesAsList = values.asList()
            [valuesAsList.first(), valuesAsList[1..-1]]
        }

    }

    String getNormalizedEnumValues(Map<String, String> enumValues) {
        enumValues.keySet().collect {
            if (it ==~ /\d+/) {
                return "" + Long.parseLong(it, 10)
            }
            return it
        }.sort().join(':')
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
