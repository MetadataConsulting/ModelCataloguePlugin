package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.SearchCatalogue
import org.modelcatalogue.core.rx.StdErrorSubscriber
import rx.Observable

import static org.modelcatalogue.core.util.HibernateHelper.*

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
                searchService.index(queued.collect{ getEntityClass(it).get(it.getId()) }).subscribe(StdErrorSubscriber.create("Exception indexing $currentChangeId"))
            }
        }

    }

    @Override
    Observable<Long> logExternalChange(CatalogueElement source, String message, Long authorId) {
        if(!parentChangeId) {
            searchService.index(source).subscribe(StdErrorSubscriber.create("Exception indexing external change for #${source.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(source)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewVersionCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe(StdErrorSubscriber.create("Exception indexing new version created for #${element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementFinalized(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe(StdErrorSubscriber.create("Exception indexing element finalized for #${element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementDeprecated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe(StdErrorSubscriber.create("Exception indexing element deprecation change for #${element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe(StdErrorSubscriber.create("Exception indexing element creation for #${element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementDeleted(CatalogueElement element, Long authorId) {
        searchService.unindex(element).subscribe(StdErrorSubscriber.create("Exception indexing element deletion for #${element.getId()}"))
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementUpdated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            searchService.index(element).subscribe(StdErrorSubscriber.create("Exception indexing element update for #${element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingCreated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe(StdErrorSubscriber.create("Exception indexing mapping creation for #${mapping.source.getId()}"))
            searchService.index(mapping.destination).subscribe(StdErrorSubscriber.create("Exception indexing mapping creation for #${mapping.destination.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingDeleted(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe(StdErrorSubscriber.create("Exception indexing mapping deletion for #${mapping.source.getId()}"))
            searchService.index(mapping.destination).subscribe(StdErrorSubscriber.create("Exception indexing mapping deletion for #${mapping.destination.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingUpdated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            searchService.index(mapping.source).subscribe(StdErrorSubscriber.create("Exception indexing mapping update for #${mapping.source.getId()}"))
            searchService.index(mapping.destination).subscribe(StdErrorSubscriber.create("Exception indexing mapping update for #${mapping.destination.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewMetadata(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe(StdErrorSubscriber.create("Exception indexing new metadata for #${extension.element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMetadataUpdated(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe(StdErrorSubscriber.create("Exception indexing updated metadata for #${extension.element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMetadataDeleted(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.element).subscribe(StdErrorSubscriber.create("Exception indexing deleted metadata for #${extension.element.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewRelation(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            searchService.index(relationship).subscribe(StdErrorSubscriber.create("Exception indexing new relation  #${relationship.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationRemoved(Relationship relationship, Long authorId) {
        searchService.unindex(relationship).subscribe(StdErrorSubscriber.create("Exception indexing removed relation  #${relationship.getId()}"))
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationArchived(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            searchService.index(relationship).subscribe(StdErrorSubscriber.create("Exception indexing archived relation  #${relationship.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe(StdErrorSubscriber.create("Exception indexing new relationship metadata  #${extension.relationship.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe(StdErrorSubscriber.create("Exception indexing updated relationship metadata  #${extension.relationship.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            searchService.index(extension.relationship).subscribe(StdErrorSubscriber.create("Exception indexing deleted relationship metadata  #${extension.relationship.getId()}"))
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }
}
