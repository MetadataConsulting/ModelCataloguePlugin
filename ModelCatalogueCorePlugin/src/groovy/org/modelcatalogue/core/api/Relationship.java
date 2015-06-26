package org.modelcatalogue.core.api;

import java.util.Map;

public interface Relationship {

    CatalogueElement getSource();
    CatalogueElement getDestination();
    RelationshipType getRelationshipType();

    Map<String, String> getExt();
}
