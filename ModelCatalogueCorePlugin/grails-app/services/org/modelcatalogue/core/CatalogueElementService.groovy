package org.modelcatalogue.core

import grails.transaction.Transactional
import org.hibernate.exception.ConstraintViolationException
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.rx.ErrorSubscriber
import org.springframework.dao.DataIntegrityViolationException
import rx.subjects.BehaviorSubject

/**
 * Business logic for {@link CatalogueElement}. This is a successor of {@link ElementService}.
 */
@Transactional
class CatalogueElementService {

    SearchCatalogue modelCatalogueSearchService

    def grailsApplication
    def cacheService

    /**
     * Deletes {@link CatalogueElement}, removes all indexes (search) and all relationships
     * (see {@link CatalogueElement#deleteRelationships()}).
     * @param catalogueElement Domain class to be deleted.
     * @throws IllegalStateException in case if any other instance reference this entity and thus cannot be deleted.
     * @throws RuntimeException in case of any unexpected error while deleting.
     */
    void delete(CatalogueElement catalogueElement) {
        def subject = BehaviorSubject.create()
        // first un-index catalogue element from search
        modelCatalogueSearchService.unindex(catalogueElement).doOnNext {
            log.debug("Unindexing for search has started before the catalogue element $catalogueElement is deleted")
        }.subscribe(subject)

        try {
            // control if manual delete of some relationships is needed
            def manualDeleteRelationships = catalogueElement
                .manualDeleteRelationships(catalogueElement instanceof DataModel ? catalogueElement : null)
            if (manualDeleteRelationships.size()) {
                throw new IllegalStateException("There are some relationships which needs to be deleted manually first " +
                                                    "${manualDeleteRelationships}")
            }

            // invalidate cache
            cacheService.invalidate(catalogueElement)

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
