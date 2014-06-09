package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.dataarchitect.Importer

class ImporterMarshaller extends AbstractMarshallers {

    ImporterMarshaller() {
        super(Importer)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                imported: el.imported,
                pendingAction: el.pendingAction,
                importQueue: el.importQueue,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
                ]
        ret
    }
}




