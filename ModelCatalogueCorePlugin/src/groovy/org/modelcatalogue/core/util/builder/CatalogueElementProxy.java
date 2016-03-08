package org.modelcatalogue.core.util.builder;

import org.modelcatalogue.core.api.CatalogueElement;

import java.util.Set;

public interface CatalogueElementProxy<T> extends CatalogueElement {
    T resolve();

    Set<RelationshipProxy> getPendingRelationships();

    CatalogueElementProxy<T> merge(CatalogueElementProxy<T> other);

    T findExisting();

    String getChanged();

    Class<T> getDomain();

    void setModelCatalogueId(String id);

    String getName();

    String getClassification();

    Object getParameter(String key);

    void setParameter(String key, Object value);

    void setExtension(String key, String value);

    void addToPendingRelationships(RelationshipProxy relationshipProxy);

    boolean isNew();

    /**
     * @return <code>false</code> if the proxy is only referenced from the builder and very likely it hasn't been
     * created by the builder
     */
    boolean isUnderControl();
}
