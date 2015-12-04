package org.modelcatalogue.core.publishing

import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.HibernateHelper

class CloningContext {

    private final DataModel dataModel
    private final DataModel destinationDataModel

    private final Set<CloningCopyAssociationsAndRelationshipsTask> pendingRelationshipsTasks = new LinkedHashSet<CloningCopyAssociationsAndRelationshipsTask>()
    private final Set<String> createdRelationshipHashes = []

    private final Map<Long, Long> clones = [:]

    CloningContext(DataModel source, DataModel destination) {
        this.dataModel = source
        this.destinationDataModel = destination
    }

    static CloningContext create(DataModel source, DataModel destination) {
        new CloningContext(source, destination)
    }

    DataModel getDestination() {
        return destinationDataModel
    }

    boolean isCloning() {
        destinationDataModel && destinationDataModel != dataModel
    }

    void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        pendingRelationshipsTasks << new CloningCopyAssociationsAndRelationshipsTask(draft, oldVersion, this)
    }

    void resolvePendingRelationships() {
        pendingRelationshipsTasks.each {
            it.copyRelationships(dataModel, createdRelationshipHashes)
        }
    }

    DataModel getDataModel() {
        return dataModel
    }

    public <E extends CatalogueElement> E findExisting(E published) {
        if (!published) {
            return published
        }

        Long publishedId = published.getId()
        Long cloneId = clones[publishedId]

        if (cloneId) {
            return (E) HibernateHelper.getEntityClass(published).get(cloneId)
        }

        DataModel theDataModel = dataModel

        List<Relationship> existing = Relationship.where {
            source.id == publishedId && relationshipType == RelationshipType.originType && destination.dataModel == theDataModel
        }.list(max: 1, sort: 'outgoingIndex')

        if (!existing) {
            return null
        }

        E clone = (E) existing.first().destination
        addClone(published, clone)
        return clone
    }

    public <E extends CatalogueElement> E preferClone(E published) {
        E existing = findExisting(published)
        if (existing) {
            return existing
        }
        published
    }

    public <E extends CatalogueElement> void addClone(E original, E clone) {
        clones[original.getId()] = clone.getId()
    }
}
