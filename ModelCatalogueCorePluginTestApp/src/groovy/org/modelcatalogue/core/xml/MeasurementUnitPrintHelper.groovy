package org.modelcatalogue.core.xml

import org.modelcatalogue.core.MeasurementUnit

/**
 * Created by ladin on 15.01.15.
 */
@Singleton
class MeasurementUnitPrintHelper extends CatalogueElementPrintHelper<MeasurementUnit> {

    @Override
    Map<String, Object> collectAttributes(MeasurementUnit element, PrintContext context) {
        Map<String, Object> attrs =  super.collectAttributes(element, context)
        if (element.symbol) {
            attrs.symbol = element.symbol
        }
        attrs
    }

    @Override
    String getTopLevelName() {
        "measurementUnit"
    }
}
