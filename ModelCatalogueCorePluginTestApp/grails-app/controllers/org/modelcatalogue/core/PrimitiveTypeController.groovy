package org.modelcatalogue.core

import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class PrimitiveTypeController extends DataTypeController<PrimitiveType> {

    PrimitiveTypeController() {
        super(PrimitiveType)
    }


    def content() {
        PrimitiveType type = PrimitiveType.get(params.id)
        if (!type) {
            notFound()
            return
        }


        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            if (type?.measurementUnit) {
                return [type.measurementUnit]
            }

            return []
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

}
