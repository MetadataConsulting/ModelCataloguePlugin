package org.modelcatalogue.core.dataimport

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.modelcatalogue.core.persistence.RelationshipGormService

class ProcessDataRowsDaoService implements ProcessDataRowsDaoReadService {

    DataClassGormService dataClassGormService
    MeasurementUnitGormService measurementUnitGormService
    PrimitiveTypeGormService primitiveTypeGormService
    DataTypeGormService dataTypeGormService
    DataModelGormService dataModelGormService
    DataElementGormService dataElementGormService
    RelationshipGormService relationshipGormService

    @Override
    DataModel findDataModelByName(String dataModelName) {
        dataModelGormService.findByName(dataModelName)
    }

    @Override
    MeasurementUnit findMeasurementUnitByModelCatalogueIdAndDataModel(String muCataId, DataModel dataModel) {
        measurementUnitGormService.findByModelCatalogueIdAndDataModel(muCatId, dataModel)
    }

    @Override
    MeasurementUnit findMeasurementUnitByNameAndDataModel(String muName, DataModel dataModel) {
        measurementUnitGormService.findByNameAndDataModel(muName, dataModel)
    }

    @Override
    MeasurementUnit findMeasurementUnitBySymbolAndDataModel(String muSymbol, DataModel dataModel) {
        measurementUnitGormService.findBySymbolAndDataModel(muSymbol, dataModel)
    }

    @Override
    DataClass findDataClassByfindByNameAndDataModel(String className, DataModel dataModel) {
        dataClassGormService.findByNameAndDataModel(className, dataModel)
    }

    @Override
    DataClass findDataClassByModelCatalogueIdAndDataModel(String className, DataModel dataModel) {
        dataClassGormService.findByModelCatalogueIdAndDataModel(className, dataModel)
    }

    @Override
    DataType findDataTypeByModelCatalogueIdAndDataModel(String dtCode, DataModel dataModel) {
        dataTypeGormService.findByModelCatalogueIdAndDataModel(dtCode, dataModel)
    }

    @Override
    DataType findDataTypeByNameAndDataModel(String dtName, DataModel dataModel) {
        dataTypeGormService.findByNameAndDataModel(dtName, dataModel)
    }

    @Override
    DataElement findDataElementByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel) {
        dataElementGormService.findByModelCatalogueIdAndDataModel(modelCatalogueId, dataModel)
    }

    @Override
    DataElement findDataElementByNameAndDataModel(String name, DataModel dataModel) {
        dataElementGormService.findDataElementByNameAndDataModel(name, dataModel)
    }

    @Override
    Relationship findRelationshipBySourceAndDestinationAndRelationshipType(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        relationshipGormService.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)
    }
}
