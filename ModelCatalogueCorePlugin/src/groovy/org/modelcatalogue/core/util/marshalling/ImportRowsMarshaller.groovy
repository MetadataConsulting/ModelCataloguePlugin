package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.ImportRow
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
                [id: it.id, parentModelName: it.parentModelName, containingModelName: it.containingModelName, dataElementName: it.dataElementName, dataType: it.dataType, measurementUnitName: it.measurementUnitName, actionLinks: getActionsLink(elements.base, it.id, it.rowActions.size()), actions: it.rowActions.collect{it.action}, imported: false, elementType: ImportRow.name]
            }else{
                [id: it.id, parentModelName: it.parentModelName, containingModelName: it.containingModelName, dataElementName: it.dataElementName, dataType: it.dataType, measurementUnitName: it.measurementUnitName, actions: it.rowActions.collect{it.action}, imported: true, elementType: ImportRow.name]
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
