package org.modelcatalogue.core

import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.modelcatalogue.core.persistence.ReferenceTypeGormService
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity

class MetadataDomainEntityService {

    AssetGormService assetGormService
    CatalogueElementGormService catalogueElementGormService
    DataClassGormService dataClassGormService
    DataElementGormService dataElementGormService
    DataModelGormService dataModelGormService
    DataTypeGormService dataTypeGormService
    EnumeratedTypeGormService enumeratedTypeGormService
    MeasurementUnitGormService measurementUnitGormService
    PrimitiveTypeGormService primitiveTypeGormService
    ReferenceTypeGormService referenceTypeGormService

    CatalogueElement findByMetadataDomainEntity(MetadataDomainEntity domainEntity) {
        switch (domainEntity.domain) {
            case MetadataDomain.ASSET:
                return assetGormService.findById(domainEntity.id)
                break
            case MetadataDomain.CATALOGUE_ELEMENT:
                return catalogueElementGormService.findById(domainEntity.id)
                break
            case MetadataDomain.DATA_CLASS:
                return dataClassGormService.findById(domainEntity.id)
                break
            case MetadataDomain.DATA_ELEMENT:
                return dataElementGormService.findById(domainEntity.id)
                break
            case MetadataDomain.DATA_MODEL:
                return dataModelGormService.findById(domainEntity.id)
                break
            case MetadataDomain.DATA_TYPE:
                return dataTypeGormService.findById(domainEntity.id)
                break
            case MetadataDomain.ENUMERATED_TYPE:
                return enumeratedTypeGormService.findById(domainEntity.id)
                break
            case MetadataDomain.MEASUREMENT_UNIT:
                return measurementUnitGormService.findById(domainEntity.id)
                break
            case MetadataDomain.PRIMITIVE_TYPE:
                return primitiveTypeGormService.findById(domainEntity.id)
                break
            case MetadataDomain.REFERENCE_TYPE:
                return referenceTypeGormService.findById(domainEntity.id)
                break
            default:
                return null
        }
    }
}
