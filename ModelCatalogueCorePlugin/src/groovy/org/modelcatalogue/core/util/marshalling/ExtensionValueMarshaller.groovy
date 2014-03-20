package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.ExtensionValue

class ExtensionValueMarshaller extends CatalogueElementMarshallers {

    ExtensionValueMarshaller() {
        super(ExtensionValue)
    }

    ExtensionValueMarshaller(Class<? extends ExtensionValue> cls) {
        super(cls)
    }


}




