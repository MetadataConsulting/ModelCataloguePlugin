package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

/**
 * Created by ladin on 18.12.14.
 */
interface CatalogueElementProxy<T extends CatalogueElement> {

    T resolve()
    Set<Relationship> resolveRelationships()
    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other)

    Class<T> getDomain()

    String getId()
    String getName()
    String getClassification()


    Object getParameter(String key)
    void setParameter(String key, Object value)
    void setExtension(String key, String value)

    void addToPendingRelationships(RelationshipProxy relationshipProxy)

}