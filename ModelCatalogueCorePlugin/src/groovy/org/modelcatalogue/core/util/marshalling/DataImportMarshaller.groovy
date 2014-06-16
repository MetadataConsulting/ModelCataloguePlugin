package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.DataImport

class DataImportMarshaller extends AbstractMarshallers {

    DataImportMarshaller() {
        super(DataImport)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                elementType: el.class.name,
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                imported: el.imported,
                pendingAction: el.pendingAction,
                importQueue: el.importQueue,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                ]
        ret
    }
}




