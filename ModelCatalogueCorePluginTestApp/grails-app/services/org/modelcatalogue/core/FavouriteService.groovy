package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
class FavouriteService {

    DataModelGormService dataModelGormService

    SpringSecurityService springSecurityService

    @CompileDynamic
    User loggedUser() {
        springSecurityService.currentUser
    }

    @Transactional
    void favouriteModel(DataModel dataModel) {
        User user = loggedUser()
        user.createLinkTo(dataModel, RelationshipType.favouriteType)
    }

    @Transactional
    void favouriteModelById(Long id) {
        DataModel dataModel = dataModelGormService.findById(id)
        if ( !dataModel ) {
            log.info ('data model not found with id: {}', id)
            return
        }
        favouriteModel(dataModel)
    }

    @Transactional
    void unfavouriteModelById(Long id) {
        DataModel dataModel = dataModelGormService.findById(id)
        if ( !dataModel ) {
            log.info ('data model not found with id: {}', id)
            return
        }
        unfavouriteModel(dataModel)
    }

    @Transactional
    void unfavouriteModel(DataModel dataModel) {
        User user = loggedUser()
        user.removeLinkTo(dataModel, RelationshipType.favouriteType)
    }
}
