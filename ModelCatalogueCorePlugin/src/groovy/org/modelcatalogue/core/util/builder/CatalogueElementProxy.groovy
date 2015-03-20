package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

interface CatalogueElementProxy<T extends CatalogueElement> {


    T resolve()
    Set<Relationship> resolveRelationships()
    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other)

    void requestDraft()
    T createDraftIfRequested()


    T findExisting()

    String getChanged()

    Class<T> getDomain()

    String getId()
    void setId(String id)

    String getName()
    String getClassification()

    Object getParameter(String key)
    void setParameter(String key, Object value)
    void setExtension(String key, String value)

    void addToPendingRelationships(RelationshipProxy relationshipProxy)

    boolean isNew()

}