package org.modelcatalogue.core

import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

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


}
