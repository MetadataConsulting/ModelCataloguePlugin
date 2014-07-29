package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.RelationshipDirection

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService implements SearchCatalogue {

    @Override
    def search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        String query = "%$params.search%"
        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type)

        switch (direction) {
            case RelationshipDirection.OUTGOING:
                criteria.destination {
                    or {
                        ilike('name', query)
                        ilike('description', query)
                    }
                }
                break
            case RelationshipDirection.INCOMING:
                criteria.source {
                    or {
                        ilike('name', query)
                        ilike('description', query)
                    }
                }
                break
            case RelationshipDirection.BOTH:
                criteria.or {
                    destination {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                        ne('id', element.id)
                    }
                    source {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                        ne('id', element.id)
                    }

                }
        }

        [searchResults: criteria.list(params), total: criteria.count()]
    }

    def search(Class resource, Map params) {
        if (!params.search) {
            return [errors: "No query string to search on"]
        }
        def searchResults = [:]

        String query = "%$params.search%"

        if (PublishedElement.isAssignableFrom(resource)) {
            DetachedCriteria criteria = new DetachedCriteria(resource)
            criteria.and {
                'in'('status', PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.UPDATED, PublishedElementStatus.FINALIZED)
                or {
                    ilike('name', query)
                    ilike('description', query)
                }
            }
            searchResults.searchResults = criteria.list(params)
            searchResults.total = criteria.count()
        } else if (CatalogueElement.isAssignableFrom(resource)) {
            searchResults.searchResults = resource.findAllByNameIlikeOrDescriptionIlike(query, query, params)
            searchResults.total = resource.countByNameIlikeOrDescriptionIlike(query, query, params)
        } else if (RelationshipType.isAssignableFrom(resource)) {
            searchResults.searchResults = resource.findAllByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query, params)
            searchResults.total = resource.countByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query, params)
        } else {
            searchResults.searchResults = resource.findAllByNameIlike(query, params)
            searchResults.total         = resource.countByNameIlike(query, params)
        }

        searchResults
    }

    def search(Map params){
        search CatalogueElement, params
    }

    def index(Class resource){}
    def index(Collection<Class> resource){}
    def unindex(Object object){}
    def unindex(Collection<Object> object){}
    def refresh(){}

}
