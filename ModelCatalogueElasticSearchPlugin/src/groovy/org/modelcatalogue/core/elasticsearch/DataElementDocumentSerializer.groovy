package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType

class DataElementDocumentSerializer extends CatalogueElementDocumentSerializer implements DocumentSerializer<DataElement> {

    Map getDocument(DataElement element) {
        Map ret = super.getDocument(element)

        if (element.dataType) {
            ret.data_type = getDataType(element.dataType)
        }

        return ret
    }

    private static Map<String, Object> getDataType(DataType dataType) {
        Map<String, Object> ret = [:]

        ret.name = dataType.name
        ret.description = dataType.description

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
