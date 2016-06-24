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

    /**
     * Deletes {@link CatalogueElement}, removes all indexes (search) and all relationships
     * (see {@link CatalogueElement#deleteRelationships()}).
     * @param catalogueElement Domain class to be deleted.
     * @throws IllegalStateException in case if any other instance reference this entity and thus cannot be deleted.
     * @throws RuntimeException in case of any unexpected error while deleting.
     */
    void delete(CatalogueElement catalogueElement) {
        def subject = BehaviorSubject.create()
        // if you delete relationships manually not using unlink you have to clear the relationship cache
        // and maybe other caches (search for com.google.common.cache.Cache)
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

            def errors = []
            if (e instanceof DataIntegrityViolationException || e instanceof ConstraintViolationException) {
                for (def property in catalogueElement.domainClass.persistentProperties) {
                    if ((property.oneToMany || property.manyToMany) && catalogueElement.hasProperty(property.name)) {
                        def value = catalogueElement[property.name]
                        if (value) {
                            def valueToLog = value
                            if (value instanceof Iterable) {
                                valueToLog = value.take(3)
                                if (value.size() > 3)
                                    valueToLog.add("...")
                            }
                            errors.add("${property.naturalName.toLowerCase()}: ${valueToLog}")
                        }
                    }
                }
            }

            if (errors.size() > 0) {
                def errorsToLog = errors.take(5)
                if (errors.size() > 5)
                    errorsToLog.add("...")
                throw new IllegalStateException("Cannot delete $catalogueElement, remove all relationship first: ${errorsToLog}")
            } else {
                throw new RuntimeException("Exception while deleting catalogue element $catalogueElement", e)
            }
        }
    }
}
