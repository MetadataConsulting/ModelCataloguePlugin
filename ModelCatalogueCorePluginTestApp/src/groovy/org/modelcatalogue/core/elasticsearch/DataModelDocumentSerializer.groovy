package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.MeasurementUnit

class DataModelDocumentSerializer extends CatalogueElementDocumentSerializer<DataModel> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataModel element, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, element, builder)

        safePut(builder, 'semantic_version', element.semanticVersion)
        safePut(builder, 'revision_notes', element.revisionNotes)

        return builder
    }
}
