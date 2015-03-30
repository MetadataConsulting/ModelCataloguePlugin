package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.OrderedMap

/**
 * Created by ladin on 14.02.14.
 */
class RelationshipMarshallers extends AbstractMarshallers {

    RelationshipMarshallers() {
        super(Relationship)
    }

    protected Map<String, Object> prepareJsonMap(rel) {
        if (!rel) return [:]
        return getRelationshipAsMap(rel)
    }

    static Map<String, Object> getRelationshipAsMap(Relationship rel) {
        [
                id: rel.id,
                source: CatalogueElementMarshallers.minimalCatalogueElementJSON(rel.source),
                destination: CatalogueElementMarshallers.minimalCatalogueElementJSON(rel.destination),
                type: rel.relationshipType.info,
                archived: rel.archived,
                ext: OrderedMap.toJsonMap(rel.ext),
                elementType: Relationship.name,
                classification: CatalogueElementMarshallers.minimalCatalogueElementJSON(rel.classification)
        ]
    }
}
