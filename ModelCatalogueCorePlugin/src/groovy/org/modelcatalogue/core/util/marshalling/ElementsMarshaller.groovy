package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.Elements

/**
 * Created by ladin on 19.02.14.
 */
class ElementsMarshaller extends ListWrapperMarshaller {

    ElementsMarshaller() {
        super(Elements)
    }

    protected String getItemNodeName() {
        "element"
    }
}
