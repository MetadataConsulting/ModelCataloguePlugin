package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsDomainClass

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

    static Classification getClassificationFromParams(params) {
       if (!params.classification) {
           return null
       }
       if (params.classification instanceof Classification) {
           return params.classification
       }
       Classification.findByUrlName(params.classification)
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
            relationshipService.link(r.source, archived, r.relationshipType, true, )
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

}
