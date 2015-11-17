package org.modelcatalogue.core.audit

import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.SearchCatalogue

class SearchNotifier extends AbstractAuditor {

    final SearchCatalogue searchService
    final Map<Long, Set<Object>> queues = [:].withDefault { new LinkedHashSet<Object>() }

    SearchNotifier(SearchCatalogue searchService) {
        this.searchService = searchService
    }

    @Override
    void setParentChangeId(Long parentChangeId) {
        Long currentChangeId = this.getParentChangeId()
        super.setParentChangeId(parentChangeId)
        if (currentChangeId && !parentChangeId) {
            Set<Object> queued = queues.remove(currentChangeId)
            if (queued) {
                searchService.index(queued.collect{ HibernateProxyHelper.getClassWithoutInitializingProxy(it).get(it.getId()) }).subscribe()
            }
        }

    }

    @Override
    Long logExternalChange(CatalogueElement source, String message, Long authorId) {
        if(!parentChangeId) {
            searchService.index(source).subscribe()
            return null
        }
        queues[parentChangeId].add(source)
        return null
    }

    @Override
    Long logNewVersionCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe()
            return null
        }
        queues[parentChangeId].add(element)
        return null
    }

    @Override
    Long logElementFinalized(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe()
            return null
        }
        queues[parentChangeId].add(element)
        return null
    }

    @Override
    Long logElementDeprecated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe()
            return null
        }
        queues[parentChangeId].add(element)
        return null
    }

    @Override
    Long logElementCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe()
            return null
        }
        queues[parentChangeId].add(element)
        return null
    }

    @Override
    Long logElementDeleted(CatalogueElement element, Long authorId) {
        searchService.unindex(element).subscribe()
        return null
    }

    @Override
    Long logElementUpdated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe()
            return null
        }
        queues[parentChangeId].add(element)
        return null
    }

    @Override
    Long logMappingCreated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe()
            searchService.index(mapping.destination).subscribe()
            return null
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return null
    }

    @Override
    Long logMappingDeleted(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe()
            searchService.index(mapping.destination).subscribe()
            return null
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return null
    }

    @Override
    Long logMappingUpdated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe()
            searchService.index(mapping.destination).subscribe()
            return null
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return null
    }

    @Override
    Long logNewMetadata(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.element)
        return null
    }

    @Override
    Long logMetadataUpdated(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.element)
        return null
    }

    @Override
    Long logMetadataDeleted(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.element)
        return null
    }

    @Override
    Long logNewRelation(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            searchService.index(relationship).subscribe()
            return null
        }
        queues[parentChangeId].add(relationship)
        return null
    }

    @Override
    Long logRelationRemoved(Relationship relationship, Long authorId) {
        searchService.unindex(relationship).subscribe()
        return null
    }

    @Override
    Long logRelationArchived(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            searchService.index(relationship).subscribe()
            return null
        }
        queues[parentChangeId].add(relationship)
        return null
    }

    @Override
    Long logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.relationship)
        return null
    }

    @Override
    Long logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe()
            return null
        }
        queues[parentChangeId].add(extension.relationship)
        return null
    }
}
