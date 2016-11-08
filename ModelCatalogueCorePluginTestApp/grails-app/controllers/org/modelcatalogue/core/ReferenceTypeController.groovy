package org.modelcatalogue.core

import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class ReferenceTypeController extends DataTypeController<ReferenceType> {

    ReferenceTypeController() {
        super(ReferenceType)
    }

    def content() {
        ReferenceType type = ReferenceType.get(params.id)
        if (!type) {
            notFound()
            return
        }


        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            if (type.dataClass) {
                return [type.dataClass]
            }
            return []
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

}
