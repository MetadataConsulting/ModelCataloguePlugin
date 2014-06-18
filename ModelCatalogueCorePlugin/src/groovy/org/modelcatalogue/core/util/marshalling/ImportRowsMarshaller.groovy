package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.ImportRows

/**
 * Created by ladin on 19.02.14.
 */
class ImportRowsMarshaller extends ListWrapperMarshaller {

    ImportRowsMarshaller() {
        super(ImportRows)
    }

    protected String getItemNodeName() {
        "element"
    }
}
