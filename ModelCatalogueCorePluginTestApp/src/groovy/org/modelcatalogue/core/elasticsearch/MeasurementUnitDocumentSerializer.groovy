package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.MeasurementUnit

class MeasurementUnitDocumentSerializer extends CatalogueElementDocumentSerializer<MeasurementUnit> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, MeasurementUnit element, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, element, builder)

        safePut(builder, 'symbol', element.symbol)

        return builder
    }
}
