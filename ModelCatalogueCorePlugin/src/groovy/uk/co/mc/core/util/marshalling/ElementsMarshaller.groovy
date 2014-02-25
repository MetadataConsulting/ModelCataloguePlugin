package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.CatalogueElement
import uk.co.mc.core.util.Elements

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
