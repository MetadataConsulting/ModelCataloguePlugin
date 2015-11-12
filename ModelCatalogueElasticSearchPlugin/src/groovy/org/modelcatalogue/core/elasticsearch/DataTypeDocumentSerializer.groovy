package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.*

class DataTypeDocumentSerializer extends CatalogueElementDocumentSerializer implements DocumentSerializer<DataType> {

    Map getDocument(DataType dataType) {
        Map ret = super.getDocument(dataType)

        if (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) {
            ret.measurement_unit = [
                    id: dataType.measurementUnit.getId(),
                    name: dataType.measurementUnit.name,
                    description: dataType.measurementUnit.description
            ]
        }

        if (dataType.instanceOf(ReferenceType) && dataType.dataClass) {
            ret.data_class = [
                    id: dataType.dataClass.getId(),
                    name: dataType.dataClass.name,
                    description: dataType.dataClass.description
            ]
        }

        if (dataType.instanceOf(EnumeratedType) && dataType.enumerations) {
            ret.enumerated_value = dataType.enumerations.collect { key, value -> [key: key, value: value] }
        }

        return ret
    }
}
