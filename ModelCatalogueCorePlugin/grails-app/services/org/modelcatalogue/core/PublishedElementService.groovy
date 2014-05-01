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
        element.updateModelCatalogueId()

        if (!element.save(flush: true)) {
            log.error(element.errors)
            throw new IllegalArgumentException("Cannot update version of $element. See application log for errors.")
        }



        archived.status = PublishedElementStatus.ARCHIVED
        archived.modelCatalogueId = archived.bareModelCatalogueId + "_" + archived.versionNumber

        if (!archived.save()) {
            log.error(archived.errors)
            throw new IllegalArgumentException("Cannot create archived version of $element. See application log for errors.")
        }

        def supersededBy = element.supersededBy

        def previousSupersededBy = supersededBy ? supersededBy[0] : null

        if (previousSupersededBy) {
            element.removeFromSupersededBy previousSupersededBy
            archived.addToSupersededBy previousSupersededBy
        }

        element.addToSupersededBy(archived)

        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            relationshipService.link(r.source, archived, r.relationshipType, true)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession') continue
            relationshipService.link(archived, r.destination, r.relationshipType, true)
        }

        if (element instanceof ExtendibleElement) {
            // TODO: this should be more generic
            archived.ext.putAll element.ext
        }

        archived
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

}
