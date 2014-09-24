package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader

class PublishedElementService {

    static transactional = true

    def grailsApplication, relationshipService, modelCatalogueSearchService

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
        archived.status = PublishedElementStatus.ARCHIVED
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

        archived.status = PublishedElementStatus.ARCHIVED
        archived.save()
    }

    public <E extends Model> E finalizeTree(Model model, Collection<Model> tree = []){

        //check that it isn't already finalized
        if(model.status==PublishedElementStatus.FINALIZED || model.status==PublishedElementStatus.ARCHIVED) return model

        //to avoid infinite loop
        if(!tree.contains(model)) tree.add(model)

        //finalize data elements
        model.contains.each{ DataElement dataElement ->
            if(dataElement.status!=PublishedElementStatus.FINALIZED && dataElement.status!=PublishedElementStatus.ARCHIVED){
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
        element.updateModelCatalogueId()

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
        archived.modelCatalogueId = archived.bareModelCatalogueId + "_" + archived.versionNumber


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

            Relationship existing = destination.outgoingRelationships.find { it.destination.name - XsdLoader.ABSTRACT_COMPLEX_TYPE_SUFFIX == rel.destination.name - XsdLoader.ABSTRACT_COMPLEX_TYPE_SUFFIX && it.relationshipType == rel.relationshipType }

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
                destination.errors.rejectValue 'outgoingRelationships', 'unable.to.transfer.relationships', [rel] as Object[],  "Unable to transfer relationship {1}"
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

            Relationship existing = destination.incomingRelationships.find { it.source.name - XsdLoader.ABSTRACT_COMPLEX_TYPE_SUFFIX== rel.source.name - XsdLoader.ABSTRACT_COMPLEX_TYPE_SUFFIX && it.relationshipType == rel.relationshipType }

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
                destination.errors.rejectValue 'incomingRelationships', 'unable.to.transfer.relationships', [rel] as Object[],  "Unable to transfer relationship {1}"
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

    Map<Long, Set<Long>> findDuplicateModelsSuggestions() {
        Object[][] results = Model.executeQuery """
            select m.id, m.name, rel.destination.name
            from Model m join m.outgoingRelationships as rel
            where
                m.status in :states
            and
                rel.archived = false
            and
                rel.relationshipType = :containment
            order by m.name asc, m.dateCreated asc, rel.destination.name asc
        """, [states: [PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.FINALIZED], containment: RelationshipType.findByName('containment')]


        Map<Long, Map<String, Object>> models = new LinkedHashMap<Long, Map<String, Object>>().withDefault { [id: it, elementNames: new TreeSet<String>()] }

        for (Object[] row in results) {
            def info = models[row[0] as Long]
            info.name = row[1] - XsdLoader.ABSTRACT_COMPLEX_TYPE_SUFFIX
            info.elementNames << row[2].toString()
            info.elementNamesSize = info.elementNames.size()
        }

        Map<Long, Set<Long>> suggestions = new LinkedHashMap<Long, Set<Long>>().withDefault { new TreeSet<Long>() }


        def current = null

        models.each { id, info ->
            if (!current) {
                current = info
            } else {
                if (info.name == current.name && info.elementNamesSize == current.elementNamesSize && info.elementNames == current.elementNames) {
                    suggestions[current.id] << info.id
                } else {
                    current = info
                }
            }
        }

        suggestions
    }


}
