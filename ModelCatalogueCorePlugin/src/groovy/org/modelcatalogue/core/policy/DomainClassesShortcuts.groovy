package org.modelcatalogue.core.policy

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.ValidationRule

trait DomainClassesShortcuts {

    static Class<CatalogueElement> getEvery() { CatalogueElement }
    static Class<CatalogueElement> getCatalogueElement() { CatalogueElement }
    static Class<DataModel> getDataModel() { DataModel }
    static Class<DataClass> getDataClass() { DataClass }
    static Class<DataElement> getDataElement() { DataElement }
    static Class<PrimitiveType> getPrimitiveType() { PrimitiveType }
    static Class<ReferenceType> getReferenceType() { ReferenceType }
    static Class<EnumeratedType> getEnumeratedType() { EnumeratedType }
    static Class<DataType> getDataType() { DataType }
    static Class<MeasurementUnit> getMeasurementUnit() { MeasurementUnit }
    static Class<ValidationRule> getValidationRule() { ValidationRule }

}
