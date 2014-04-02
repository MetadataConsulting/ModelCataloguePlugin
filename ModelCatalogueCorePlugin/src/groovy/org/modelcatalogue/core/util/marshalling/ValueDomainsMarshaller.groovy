package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.ValueDomains

/**
 * Created by ladin on 19.02.14.
 */
class ValueDomainsMarshaller extends ListWrapperMarshaller {

    ValueDomainsMarshaller() {
        super(ValueDomains)
    }

    protected String getItemNodeName() {
        "valueDomain"
    }
}
