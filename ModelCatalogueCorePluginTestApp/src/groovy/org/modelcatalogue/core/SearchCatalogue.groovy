package org.modelcatalogue.core

import org.modelcatalogue.core.util.ParamArgs
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection
import rx.Observable

public interface SearchCatalogue {

    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, String search, String status, Long dataModelId, ParamArgs paramArgs)
    public <T> ListWithTotalAndType<T> search(Class<T> resource, String search, String status, Long dataModelId, ParamArgs paramArgs)
    ListWithTotalAndType<CatalogueElement> search(String search, String status, Long dataModelId, ParamArgs paramArgs)

    Observable<Boolean> index(Object element)
    Observable<Boolean> index(Iterable<Object> resource)
    Observable<Boolean> unindex(Object object)
    Observable<Boolean> unindex(Collection<Object> object)
    Observable<Boolean> reindex(boolean soft)
    void deleteIndexes()

    boolean isIndexingManually()

}
