package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.Import

class ImportMarshaller extends AbstractMarshallers {

    ImportMarshaller() {
        super(Import)
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




