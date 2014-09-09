package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.dataarchitect.DataImport
import org.modelcatalogue.core.dataarchitect.ImportRow

class DataImportMarshaller extends AbstractMarshallers {

    DataImportMarshaller() {
        super(DataImport)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                name: el.name,
                elementType: el.class.name,
                imported: [count: el?.imported ? el.imported.size() : 0 , itemType: ImportRow.name, link: "/dataArchitect/imports/$el.id/imported"],
                pendingAction: [count: el?.pendingAction ? el.pendingAction.size() : 0 , itemType: ImportRow.name, link: "/dataArchitect/imports/$el.id/pendingAction"],
                importQueue: [count: el?.importQueue ? el.importQueue.size() : 0 , itemType: ImportRow.name, link: "/dataArchitect/imports/$el.id/importQueue"],
                link:  "/dataArchitect/imports/$el.id",
        ]
        ret
    }
}

