package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

class PublishedElementService {

    static transactional = true

    def grailsApplication
    def relationshipService
    def modelCatalogueSearchService
    def messageSource

    List<PublishedElement> list(params = [:]) {
        PublishedElement.findAllByStatus(getStatusFromParams(params), params)
    }

    public <E extends PublishedElement>  List<E> list(params = [:], Class<E> resource) {
        resource.findAllByStatus(getStatusFromParams(params), params)
    }

    Long count(params = [:]) {
        PublishedElement.countByStatus(getStatusFromParams(params))
    }

    public <E extends PublishedElement>  Long count(params = [:], Class<E> resource) {
        resource.countByStatus(getStatusFromParams(params))
    }

    public <E extends PublishedElement> E archiveAndIncreaseVersion(PublishedElement element) {
        if (element.archived) throw new IllegalArgumentException("You cannot archive already archived element $element")

        GrailsDomainClass domainClass = grailsApplication.getDomainClass(element.class.name)

        E archived = element.class.newInstance()

        for (prop in domainClass.persistentProperties) {
            if (!prop.association) {
                archived[prop.name] = element[prop.name]
            }
        }

        element = createNewVersion(element)
        archived = populateArchivedProperties(archived, element)

        element.status = PublishedElementStatus.DRAFT
        element.save()

        def supersedes = element.supersedes
        def previousSupersedes = supersedes ? supersedes[0] : null
        if (previousSupersedes) {
            element.removeFromSupersedes previousSupersedes
            archived.addToSupersedes previousSupersedes
        }

        element.addToSupersedes(archived)

        archived = addRelationshipsToArchived(archived, element)
        archived = elementSpecificActions(archived, element)

        modelCatalogueSearchService.unindex(archived)

        //set archived status from updated to archived
        archived.status = PublishedElementStatus.DEPRECATED
        archived.latestVersion = element.latestVersion ?: element
        archived.save()
    }

    public <E extends PublishedElement> E archive(PublishedElement archived) {
        if (archived.archived) throw new IllegalArgumentException("You cannot archive already archived element $element")

        archived.incomingRelationships.each {
            it.archived = true
            it.save(failOnError: true)
        }

        archived.outgoingRelationships.each {
            it.archived = true
            it.save(failOnError: true)
        }

        modelCatalogueSearchService.unindex(archived)

        archived.status = PublishedElementStatus.DEPRECATED
        archived.save()
    }

    public <E extends Model> E finalizeTree(Model model, Collection<Model> tree = []){

        //check that it isn't already finalized
        if(model.status==PublishedElementStatus.FINALIZED || model.status==PublishedElementStatus.DEPRECATED) return model

        //to avoid infinite loop
        if(!tree.contains(model)) tree.add(model)

        //finalize data elements
        model.contains.each{ DataElement dataElement ->
            if(dataElement.status!=PublishedElementStatus.FINALIZED && dataElement.status!=PublishedElementStatus.DEPRECATED){
                dataElement.status = PublishedElementStatus.FINALIZED
                dataElement.save(flush:true)
            }
        }

        //finalize child models
        model.parentOf.each{ Model child ->
            if(!tree.contains(child)) {
                finalizeTree(child, tree)
            }
        }

        model.status = PublishedElementStatus.FINALIZED
        model.save(flush:true)

        return model

    }

    static PublishedElementStatus getStatusFromParams(params) {
        if (!params.status) {
            return PublishedElementStatus.FINALIZED
        }
        if (params.status instanceof PublishedElementStatus) {
            return params.status
        }
        return PublishedElementStatus.valueOf(params.status.toString().toUpperCase())
    }

    private PublishedElement createNewVersion(PublishedElement element){
        element.versionNumber++
        element.versionCreated = new Date()

        if (!element.latestVersion) {
            element.latestVersion = element
        }

        if (!element.save(flush: true)) {
            log.error(element.errors)
            throw new IllegalArgumentException("Cannot update version of $element. See application log for errors.")
        }

        element
    }

    private PublishedElement elementSpecificActions(PublishedElement archived, PublishedElement element){

        //don't add parent relationships to new version of model - this should be manually done
        //children on the other hand should be added
        if(element instanceof Model) {
            if(element.childOf.size() > 0){
                element.childOf.each{ Model model ->
                    relationshipService.unlink(model, element, RelationshipType.hierarchyType, true)
                }
            }
        }


        //don't add a data element to the model if it's updated (the old model should still reference the archived one)
        if(element instanceof DataElement) {
            if(element.containedIn.size() > 0){
                element.containedIn.each{ Model model ->
                    relationshipService.unlink(model, element, RelationshipType.containmentType, true)
                }
            }
            if (element.valueDomain) {
                archived.valueDomain = element.valueDomain
            }
        }

        //add all the extensions to the archived element as well
        if (element instanceof ExtendibleElement) {
            // TODO: this should be more generic
            archived.ext.putAll element.ext
        }

        archived
    }

    private PublishedElement addRelationshipsToArchived(PublishedElement archived, PublishedElement element){
        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            if (r.archived || r.relationshipType.name == 'hierarchy' || r.relationshipType.name == 'containment') {
                relationshipService.link(r.source, archived, r.relationshipType, false, true)
                continue
            }
            relationshipService.link(r.source, archived, r.relationshipType, true)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            if (r.archived || r.relationshipType.name == 'hierarchy') {
                relationshipService.link(archived, r.destination, r.relationshipType, false, true)
                continue
            }
            relationshipService.link(archived, r.destination, r.relationshipType, true)
        }

        archived
    }

    private PublishedElement populateArchivedProperties(PublishedElement archived, PublishedElement element){
        //set archived as updated whilst updates are going on (so it doesn't interfere with regular validation rules)
        archived.status = PublishedElementStatus.UPDATED
        archived.dateCreated = element.dateCreated // keep the original creation date


        if (!archived.save()) {
            log.error(archived.errors)
            throw new IllegalArgumentException("Cannot create archived version of $element. See application log for errors.")
        }
        archived
    }

    public <E extends PublishedElement> E merge(E source, E destination, Set<Classification> classifications = new HashSet(source.classifications)) {
        log.info "Merging $source into $destination"
        if (destination == null) return null

        if (source == null) {
            destination.errors.reject('merge.source.missing', 'Source is missing')
            return destination
        }

        // fallthrough if already merge in progress or the source and destination are the same
        if (source == destination || destination.status == PublishedElementStatus.UPDATED || source.status == PublishedElementStatus.UPDATED) {
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

        PublishedElementStatus originalStatus = destination.status

        GrailsDomainClass grailsDomainClass = grailsApplication.getDomainClass(source.class.name)

        for (GrailsDomainClassProperty property in grailsDomainClass.persistentProperties) {
            if (property.manyToOne || property.oneToOne) {
                def dstProperty = destination[property.name]
                def srcProperty = source[property.name]

                if (dstProperty && srcProperty && dstProperty != dstProperty) {
                    destination.errors.rejectValue property.name, 'merge.both.set.' + property.name, "Property '$property.name' is set in both source and destination. Delete it prior the merge."
                    return destination
                }

                destination[property.name] = dstProperty ?: srcProperty
            }
        }

        destination.validate()

        if (destination.hasErrors()) {
            return destination
        }

        destination.status = PublishedElementStatus.UPDATED
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
                if (rel.destination instanceof PublishedElement && existing.destination instanceof PublishedElement && rel.destination.class == existing.destination.class && existing.destination != destination) {
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
                if (rel.source instanceof PublishedElement && existing.source instanceof PublishedElement && rel.source.class == existing.source.class && existing.source != destination) {
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
        """, [states: [PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.FINALIZED], containment: RelationshipType.findByName('containment'), hierarchy: RelationshipType.findByName('hierarchy')]


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

    Map<Long, Set<Long>> findDuplicateDataElementsSuggestions() {
        // TODO: create test
        Object[][] results = DataElement.executeQuery """
            select count(de.id), de.name, vd.id, vd.name
                from DataElement de join de.valueDomain vd
                where
                    de.status in :states
                group by de.name, vd.id
                having count(de.id) > 1
        """, [states: [PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.FINALIZED]]

        Map<Long, Set<Long>> elements = new LinkedHashMap<Long, Set<Long>>()

        results.each { row ->
            Long[] duplicates = DataElement.executeQuery """
                    select de.id from DataElement de
                    where de.name = :name
                    and de.valueDomain.id = :vd
                    and de.status in :states

                    order by de.dateCreated
            """, [name: row[1], vd: row[2], states: [PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.FINALIZED]]

            elements[duplicates.head()] = new HashSet<Long>(duplicates.tail().toList())

        }

        elements
    }


}
