package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.rx.ErrorSubscriber
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import rx.subjects.BehaviorSubject

/**
 * Business logic for {@link CatalogueElement}. This is a successor of {@link ElementService}.
 */
@Transactional
class CatalogueElementService {

    SearchCatalogue modelCatalogueSearchService

    ManualDeleteRelationshipsService manualDeleteRelationshipsService

    def grailsApplication
    def cacheService

    static <T extends CatalogueElement> ListWrapper<T> getAllVersions(Map<String, Object> customParams = ImmutableMap.of('sort', 'semanticVersion'), T element) {
        Class<T> type = HibernateHelper.getEntityClass(element)
        String name = GrailsNameUtils.getPropertyName(type)

        if (name in ['primitiveType', 'enumeratedType', 'referenceType']) {
            name = 'dataType'
            type = DataType
        }

        final Long id = element.id
        final Long latestVersionId = element.latestVersionId
        final String base = "/${name}/${element.id}/history"

        if (!latestVersionId) {
            return Lists.wrap(customParams, base, Lists.lazy(customParams, type, {
                [type.get(id)]
            }, { 1 }))
        }

        Lists.fromCriteria(customParams, type, base) {
            eq 'latestVersionId', latestVersionId
        }
    }

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

            List<CatalogueElementDeleteBlocker> manualDeleteRelationships = []
            if ( catalogueElement instanceof DataModel ) {
                manualDeleteRelationships = manualDeleteRelationshipsService.manualDeleteRelationships(catalogueElement as DataModel)
            } else {
                List<DeleteBlocker> deleteBlockerList = manualDeleteRelationshipsService.manualDeleteRelationshipsAtCatalogueElement(catalogueElement, null)
                if ( deleteBlockerList ) {
                    manualDeleteRelationships = [new CatalogueElementDeleteBlocker(elementTargetedToDeletion: catalogueElement, deleteBlockerList: deleteBlockerList)]
                }
            }

            if (manualDeleteRelationships.size()) {
                throw new IllegalStateException("There are some relationships which needs to be deleted manually first " +
                                                    "${manualDeleteRelationships}")
            }

            Long id = catalogueElement.getId()
            Long latestVersionId = catalogueElement.getLatestVersionId() ?: catalogueElement.getId()

            // remove all associations
            catalogueElement.deleteRelationships()

            // delete the catalogue element
            catalogueElement.delete()

            // invalidate cache
            cacheService.invalidate(id, latestVersionId)

            subject.subscribe(ErrorSubscriber.create("Error during unindexing catalogue element $catalogueElement"))
        } catch (e) {
            // index catalogue element back in case of any error
            subject.flatMap {
                modelCatalogueSearchService.index(catalogueElement)
            }.subscribe(ErrorSubscriber.create("Error during indexing catalogue element $catalogueElement"))

            throw new RuntimeException("Exception while deleting catalogue element $catalogueElement", e)
        }
    }
}
