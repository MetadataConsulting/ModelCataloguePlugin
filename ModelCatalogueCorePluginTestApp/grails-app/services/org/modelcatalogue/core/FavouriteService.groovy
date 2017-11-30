package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.security.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
class FavouriteService {

    DataModelGormService dataModelGormService

    DataTypeGormService dataTypeGormService

    DataElementGormService dataElementGormService

    DataClassGormService dataClassGormService

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
    void favouriteDataClass(DataClass dataClass) {
        User user = loggedUser()
        user.createLinkTo(dataClass, RelationshipType.favouriteType)
    }

    @Transactional
    void favouriteDataElement(DataElement dataElement) {
        User user = loggedUser()
        user.createLinkTo(dataElement, RelationshipType.favouriteType)
    }

    @Transactional
    void favouriteDataType(DataType dataType) {
        User user = loggedUser()
        user.createLinkTo(dataType, RelationshipType.favouriteType)
    }

    @Transactional
    void favouriteElementTypeById(String elementType, Long elementId) {
        if ( elementType == 'org.modelcatalogue.core.DataClass' ) {
            DataClass dataClass = dataClassGormService.findById(elementId)
            favouriteDataClass(dataClass)

        } else if ( elementType == 'org.modelcatalogue.core.DataElement' ) {
            DataElement dataElement = dataElementGormService.findById(elementId)
            favouriteDataElement(dataElement)

        } else if ( elementType == 'org.modelcatalogue.core.DatType' ) {
            DataType dataType = dataTypeGormService.findById(elementId)
            favouriteDataType(dataType)

        } else if ( elementType == 'org.modelcatalogue.core.DataModel' ) {
            DataModel dataModel = dataModelGormService.findById(elementId)
            favouriteModel(dataModel)
        }
    }

    @Transactional
    void unfavouriteElementTypeById(String elementType, Long elementId) {
        if ( elementType == 'org.modelcatalogue.core.DataClass' ) {
            DataClass dataClass = dataClassGormService.findById(elementId)
            unfavouriteDataClass(dataClass)

        } else if ( elementType == 'org.modelcatalogue.core.DataElement' ) {
            DataElement dataElement = dataElementGormService.findById(elementId)
            unfavouriteDataElement(dataElement)

        } else if ( elementType == 'org.modelcatalogue.core.DatType' ) {
            DataType dataType = dataTypeGormService.findById(elementId)
            unfavouriteDataType(dataType)

        } else if ( elementType == 'org.modelcatalogue.core.DataModel' ) {
            DataModel dataModel = dataModelGormService.findById(elementId)
            unfavouriteModel(dataModel)
        }
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

    @Transactional
    void unfavouriteDataClass(DataClass dataClass) {
        User user = loggedUser()
        user.removeLinkTo(dataClass, RelationshipType.favouriteType)
    }

    @Transactional
    void unfavouriteDataElement(DataElement dataElement) {
        User user = loggedUser()
        user.removeLinkTo(dataElement, RelationshipType.favouriteType)
    }

    @Transactional
    void unfavouriteDataType(DataType dataType) {
        User user = loggedUser()
        user.removeLinkTo(dataType, RelationshipType.favouriteType)
    }


}
