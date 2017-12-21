package org.modelcatalogue.core.dataimport

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

interface ProcessDataRowsDaoReadService {
    DataModel findDataModelByName(String dataModelName)
    MeasurementUnit findMeasurementUnitByModelCatalogueIdAndDataModel(String muCataId, DataModel dataModel)
    MeasurementUnit findMeasurementUnitByNameAndDataModel(String muName, DataModel dataModel)
    MeasurementUnit findMeasurementUnitBySymbolAndDataModel(String muSymbol, DataModel dataModel)
    DataClass findDataClassByfindByNameAndDataModel(String className, DataModel dataModel)
    DataClass findDataClassByModelCatalogueIdAndDataModel(String className, DataModel dataModel)
    DataType findDataTypeByModelCatalogueIdAndDataModel(String dtCode, DataModel dataModel)
    DataType findDataTypeByNameAndDataModel(String dtName, DataModel dataModel)
    DataElement findDataElementByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel)
    DataElement findDataElementByNameAndDataModel(String name, DataModel dataModel)
    Relationship findRelationshipBySourceAndDestinationAndRelationshipType(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType)
}