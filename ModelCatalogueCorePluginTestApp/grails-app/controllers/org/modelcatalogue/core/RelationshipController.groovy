package org.modelcatalogue.core

import org.springframework.http.HttpStatus

class RelationshipController {

    static responseFormats = ['json']

    def modelCatalogueSecurityService

    //TODO: check that we need this method at all
    //not sure that we do
    //only allowing supervisors to do this
    def restore() {
        Long id = params.long('id')

        if (!id) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Relationship relationship = Relationship.get(id)

        if (!relationship) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        relationship.archived = false
        relationship.save()

        if (relationship.hasErrors()) {
            respond relationship.errors
            return
        }

        respond relationship
    }

}
