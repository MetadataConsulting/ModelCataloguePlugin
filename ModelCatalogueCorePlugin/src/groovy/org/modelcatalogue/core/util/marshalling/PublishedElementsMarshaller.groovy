package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.PublishedElements

/**
 * Created by ladin on 19.02.14.
 */
class PublishedElementsMarshaller extends ListWrapperMarshaller {

    PublishedElementsMarshaller() {
        super(PublishedElements)
    }

    protected String getItemNodeName() {
        "publishedElement"
    }
}
