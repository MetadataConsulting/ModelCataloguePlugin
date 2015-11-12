package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.MeasurementUnit

class MeasurementUnitDocumentSerializer extends CatalogueElementDocumentSerializer implements DocumentSerializer<MeasurementUnit> {

    Map getDocument(MeasurementUnit unit) {
        Map ret = super.getDocument(unit)

        ret.symbol = unit.symbol

        return ret
    }

}
