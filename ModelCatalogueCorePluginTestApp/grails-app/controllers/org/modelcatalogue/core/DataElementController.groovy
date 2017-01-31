package org.modelcatalogue.core

import grails.util.Environment
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

    DataElementService dataElementService

    DataElementController() {
        super(DataElement, false)
    }

    def content() {
        DataElement dataElement = DataElement.get(params.id)
        if (!dataElement) {
            notFound()
            return
        }


        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            if (dataElement.dataType) {
                return [dataElement.dataType]
            }
            return []
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    @Override
    protected ListWrapper<DataElement> getAllEffectiveItems(Integer max) {
        if (!params.boolean("dataModel") || Environment.current != Environment.PRODUCTION) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataElementService.findAllDataElementsInModel(params, DataModel.get(params.long('dataModel'))))
    }


}
