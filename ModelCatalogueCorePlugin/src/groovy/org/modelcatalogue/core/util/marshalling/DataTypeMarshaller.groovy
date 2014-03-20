package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.DataType

class DataTypeMarshaller extends CatalogueElementMarshallers {

    DataTypeMarshaller() {
        super(DataType)
    }

    DataTypeMarshaller(Class<? extends DataType> cls) {
        super(cls)
    }


}




