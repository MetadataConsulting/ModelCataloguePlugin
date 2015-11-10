package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection

public interface SearchCatalogue {

    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params)
    public <T> ListWithTotalAndType<T> search(Class<T> resource, Map params)
    ListWithTotalAndType<CatalogueElement> search(Map params)

    void index(Object element)
    void index(Iterable<Object> resource)
    void unindex(Object object)
    void unindex(Collection<Object> object)
    void refresh()

}