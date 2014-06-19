package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ImportRows

/**
 * Created by ladin on 19.02.14.
 */
class ImportRowsMarshaller extends ListWrapperMarshaller {

    ImportRowsMarshaller() {
        super(ImportRows)
    }


    protected Map<String, Object> prepareJsonMap(Object elements) {
        if (!elements) return [:]
        def ret = super.prepareJsonMap(elements)
        ret.list = elements.items.collect{
            [id: it.id, containingModelName: it.containingModelName, dataElementName: it.dataElementName, resolveAllLink: "${elements.base}/${it.id}/resolveAll", actions: el.rowActions.collect{it.action}]
        }
        ret
    }


    protected String getItemNodeName() {
        "element"
    }
}
