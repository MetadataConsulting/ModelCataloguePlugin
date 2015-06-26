package org.modelcatalogue.core.api;

import java.util.List;
import java.util.Map;

public interface CatalogueElement {

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getModelCatalogueId();
    void setModelCatalogueId(String modelCatalogueId);

    ElementStatus getStatus();
    void setStatus(ElementStatus status);


    List<Relationship> getIncomingRelationshipsByType(RelationshipType type);
    List<Relationship> getOutgoingRelationshipsByType(RelationshipType type);
    List<Relationship> getRelationshipsByType(RelationshipType type);

    int countIncomingRelationshipsByType(RelationshipType type);
    int countOutgoingRelationshipsByType(RelationshipType type);
    int countRelationshipsByType(RelationshipType type);

    Relationship createLinkTo(Map<String, Object> parameters, CatalogueElement destination, RelationshipType type);
    Relationship createLinkFrom(Map<String, Object> parameters, CatalogueElement source, RelationshipType type);

    Relationship removeLinkTo(CatalogueElement destination, RelationshipType type);
    Relationship removeLinkFrom(CatalogueElement source, RelationshipType type);

    Map<String, String> getExt();
}
