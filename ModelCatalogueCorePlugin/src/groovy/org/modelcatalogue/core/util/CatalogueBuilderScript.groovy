package org.modelcatalogue.core.util

import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain

abstract class CatalogueBuilderScript extends Script {

    @Delegate CatalogueBuilder delegate

    abstract configure()

    @Override Object run() {
        delegate = binding.builder
        configure()
    }

    // for some unknown reason this must be repeated here
    Class<Classification> getClassification() { Classification }
    Class<Model> getModel() { Model }
    Class<DataElement> getDataElement() { DataElement }
    Class<ValueDomain> getValueDomain() { ValueDomain }
    Class<DataType> getDataType() { DataType }
    Class<MeasurementUnit> getMeasurementUnit() { MeasurementUnit }
}
