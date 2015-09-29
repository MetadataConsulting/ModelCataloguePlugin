package org.modelcatalogue.core

import org.springframework.http.HttpStatus

class RelationshipController {

    static responseFormats = ['json']

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
