package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.Elements

/**
 * Created by ladin on 19.02.14.
 */
class DataImportsMarshaller extends ListWrapperMarshaller {

    DataImportsMarshaller() {
        super(Elements)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.base =
        ret
    }

    protected String getItemNodeName() {
        "element"
    }
}
