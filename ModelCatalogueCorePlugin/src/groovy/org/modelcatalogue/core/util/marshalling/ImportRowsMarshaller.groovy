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
            if(!it.imported){
                [id: it.id, containingModelName: it.containingModelName, dataElementName: it.dataElementName, actionLinks: getActionsLink(elements.base, it.id, it.rowActions.size()), actions: it.rowActions.collect{it.action}]
            }else{
                [id: it.id, containingModelName: it.containingModelName, dataElementName: it.dataElementName, actions: it.rowActions.collect{it.action}]
            }
        }
        ret
    }


    protected String getItemNodeName() {
        "element"
    }

    private static getList(Object elements){


    }

    private static getActionsLink(String base, Long id, Long actionCount){
        def actionLink
        if(actionCount>0) {
            actionLink = "${base}/${id}/resolveAllRowActions"
        }else {
            actionLink = "${base}/${id}/ingestRow"
        }
        return actionLink
    }
}
