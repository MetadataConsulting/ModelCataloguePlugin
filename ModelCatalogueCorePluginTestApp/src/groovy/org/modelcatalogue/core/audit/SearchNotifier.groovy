package org.modelcatalogue.core.audit

import static org.modelcatalogue.core.util.HibernateHelper.*
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.SearchCatalogue
import org.modelcatalogue.core.rx.ErrorSubscriber
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.Subject

import java.util.concurrent.TimeUnit

class SearchNotifier extends AbstractAuditor {

    final SearchCatalogue searchService
    final Map<Long, Set<Object>> queues = [:].withDefault { new LinkedHashSet<Object>() }
    final Subject<Object, Object> buffer = PublishSubject.create()

    SearchNotifier(SearchCatalogue searchService) {
        this.searchService = searchService
        buffer.buffer(200L, TimeUnit.MILLISECONDS).filter{ !it.isEmpty() }.map {
            new HashSet<Object>(it)
        } .flatMap { queued ->
            searchService.index(queued)
        }.subscribe(ErrorSubscriber.create("Exception during batch index"))
    }

    private void indexInternal(Object object) {
        if (!getSystem()) {buffer.onNext(object)}
    }

    @Override
    void setParentChangeId(Long parentChangeId) {
        Long currentChangeId = this.getParentChangeId()
        super.setParentChangeId(parentChangeId)
        if (currentChangeId && !parentChangeId) {
            Set<Object> queued = queues.remove(currentChangeId)
            if (queued) {
                searchService.index(queued.collect{ getEntityClass(it).get(it.getId()) }).subscribe(ErrorSubscriber.create("Exception indexing $currentChangeId"))
            }
        }

    }

    @Override
    Observable<Long> logExternalChange(CatalogueElement source, String message, Long authorId) {
        if(!parentChangeId) {
            indexInternal(source)
            return Observable.empty()
        }
        queues[parentChangeId].add(source)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewVersionCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            indexInternal(element)
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementFinalized(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            indexInternal(element)
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementDeprecated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            indexInternal(element)
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementCreated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            indexInternal(element)
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementDeleted(CatalogueElement element, Long authorId) {
        searchService.unindex(element).subscribe(ErrorSubscriber.create("Exception indexing element deletion for #${element.getId()}"))
        return Observable.empty()
    }

    @Override
    Observable<Long> logElementUpdated(CatalogueElement element, Long authorId) {
        if(!parentChangeId) {
            indexInternal(element)
            return Observable.empty()
        }
        queues[parentChangeId].add(element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingCreated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            indexInternal(mapping.source)
            indexInternal(mapping.destination)
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingDeleted(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            indexInternal(mapping.source)
            indexInternal(mapping.destination)
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMappingUpdated(Mapping mapping, Long authorId) {
        if(!parentChangeId) {
            indexInternal(mapping.source)
            indexInternal(mapping.destination)
            return Observable.empty()
        }
        queues[parentChangeId].add(mapping.source)
        queues[parentChangeId].add(mapping.destination)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewMetadata(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.element)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMetadataUpdated(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.element)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logMetadataDeleted(ExtensionValue extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.element)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.element)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewRelation(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            indexInternal(relationship)
            return Observable.empty()
        }
        queues[parentChangeId].add(relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationRemoved(Relationship relationship, Long authorId) {
        searchService.unindex(relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationArchived(Relationship relationship, Long authorId) {
        if(!parentChangeId) {
            indexInternal(relationship)
            return Observable.empty()
        }
        queues[parentChangeId].add(relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logNewRelationshipMetadata(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.relationship)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationshipMetadataUpdated(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.relationship)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }

    @Override
    Observable<Long> logRelationshipMetadataDeleted(RelationshipMetadata extension, Long authorId) {
        if(!parentChangeId) {
            indexInternal(extension.relationship)
            return Observable.empty()
        }
        queues[parentChangeId].add(extension.relationship)
        return Observable.empty()
    }
}
