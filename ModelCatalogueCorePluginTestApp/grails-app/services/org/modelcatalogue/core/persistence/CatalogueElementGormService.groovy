package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

class CatalogueElementGormService {

    MeasurementUnitGormService measurementUnitGormService

    DataTypeGormService dataTypeGormService

    DataClassGormService dataClassGormService

    DataElementGormService dataElementGormService

    UserGormService userGormService

    TagGormService tagGormService

    AssetGormService assetGormService

    ValidationRuleGormService validationRuleGormService

    DataModelGormService dataModelGormService

    @Transactional
    CatalogueElement findById(long id) {
        CatalogueElement.get(id)
    }

    @Transactional
    CatalogueElement findCatalogueElementByClassAndId(Class catalogueElementClass, Long id) {
        if ( catalogueElementClass == DataModel ) {
            return dataModelGormService.findById(id)

        } else if ( catalogueElementClass == MeasurementUnit) {
            return measurementUnitGormService.findById(id)

        } else if ( catalogueElementClass == DataType ) {
            return dataTypeGormService.findById(id)

        } else if ( catalogueElementClass == DataClass ) {
            return dataClassGormService.findById(id)

        } else if ( catalogueElementClass == DataElement) {
            return dataElementGormService.findById(id)

        } else if ( catalogueElementClass == User) {
            return userGormService.findById(id)

        } else if ( catalogueElementClass == Tag ) {
            return tagGormService.findById(id)

        } else if ( catalogueElementClass == Asset ) {
            return assetGormService.findById(id)

        } else if ( catalogueElementClass == ValidationRule ) {
            return validationRuleGormService.findById(id)

        } else if ( catalogueElementClass == DataModel ) {
            return dataModelGormService.findById(id)
        }
        return null
    }

    @Transactional
    void updateCatalogueElementStatus(CatalogueElement catalogueElement, ElementStatus status) {
            catalogueElement.status = status
            catalogueElement.save(flush: true)
    }
}
