package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.DetachedListWrapper

/**
 * Created by ladin on 19.02.14.
 */
class DetachedListWrapperMarshaller extends ListWrapperMarshaller {

    DetachedListWrapperMarshaller() {
        super(DetachedListWrapper)
    }

    protected String getItemNodeName() {
        "element"
    }
}
