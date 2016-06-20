package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.rx.ErrorSubscriber
import rx.subjects.BehaviorSubject

/**
 * Business logic for {@link CatalogueElement}. This is a successor of {@link ElementService}.
 */
@Transactional
class CatalogueElementService {

    SearchCatalogue modelCatalogueSearchService

    /**
     * Deletes {@link CatalogueElement}, removes all indexes (search) and all relationships
     * (see {@link CatalogueElement#deleteRelationships()}).
     * @param catalogueElement Domain class to be deleted.
     */
    void delete(CatalogueElement catalogueElement) {
        def subject = BehaviorSubject.create()

        // first un-index catalogue element from search
        modelCatalogueSearchService.unindex(catalogueElement).doOnNext {
            log.debug("Unindexing for search has started before the catalogue element $catalogueElement is deleted")
        }.subscribe(subject)

        try {
            // remove all associations
            catalogueElement.deleteRelationships()

            // delete the catalogue element
            catalogueElement.delete()
        } catch (e) {
            // index catalogue element back in case of any error
            subject.flatMap {
                modelCatalogueSearchService.index(catalogueElement)
            }.subscribe(ErrorSubscriber.create("Error during indexing catalogue element $catalogueElement"))

            throw new RuntimeException("Exception while deleting catalogue element $catalogueElement", e)
        }
    }
}
