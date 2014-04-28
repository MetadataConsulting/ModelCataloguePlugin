package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsDomainClass

@Transactional
class PublishedElementService {

    def grailsApplication
    def relationshipService

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

        element.versionNumber++

        def newCatalogueId =  element.modelCatalogueId.split("_")
        newCatalogueId[-1] = newCatalogueId.last().toInteger() + 1
        element.modelCatalogueId = newCatalogueId.join("_")

        if (!element.save(flush: true)) {
            log.error(element.errors)
            throw new IllegalArgumentException("Cannot update version of $element. See application log for errors.")
        }

        archived.status = PublishedElementStatus.ARCHIVED


        if (!archived.save()) {
            log.error(archived.errors)
            throw new IllegalArgumentException("Cannot create archived version of $element. See application log for errors.")
        }

        //if the item is a data element contained in a model, update and increase the model version
        //providing the model isn't pending updates. If the model is pending updates i.e. during an import
        //we don't want to increase version with every data element change only at the end of all the changes

        if(element instanceof DataElement){
            element.containedIn.each{ Model model ->
                if(model.status!= PublishedElementStatus.PENDING){
                    Model archivedModel = archiveAndIncreaseVersion(model)
                    archivedModel.removeFromContains(element)
                    archivedModel.addToContains(archived)
                }
            }
        }

        def supersedes = element.supersedes

        def previousSupersedes = supersedes ? supersedes[0] : null

        if (previousSupersedes) {
            element.removeFromSupersedes previousSupersedes
            archived.addToSupersedes previousSupersedes
        }

        element.addToSupersedes(archived)

        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            relationshipService.link(r.source, archived, r.relationshipType, true)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            relationshipService.link(archived, r.destination, r.relationshipType, true)
        }

        archived
    }


    private static PublishedElementStatus getStatusFromParams(params) {
        if (!params.status) {
            return PublishedElementStatus.FINALIZED
        }
        if (params.status instanceof PublishedElementStatus) {
            return params.status
        }
        return PublishedElementStatus.valueOf(params.status.toString().toUpperCase())
    }

}
