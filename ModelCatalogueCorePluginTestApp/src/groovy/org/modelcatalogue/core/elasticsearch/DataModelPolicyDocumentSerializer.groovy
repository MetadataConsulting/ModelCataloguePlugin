package org.modelcatalogue.core.elasticsearch

import static org.modelcatalogue.core.elasticsearch.CatalogueElementDocumentSerializer.safePut
import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.DataModelPolicy
import org.modelcatalogue.core.Relationship

class DataModelPolicyDocumentSerializer implements DocumentSerializer<DataModelPolicy> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataModelPolicy relationship, ImmutableMap.Builder<String, Object> builder) {
        safePut(builder, 'name', relationship.name)
        safePut(builder, 'policy_text', relationship.policyText)
        return builder
    }
}
