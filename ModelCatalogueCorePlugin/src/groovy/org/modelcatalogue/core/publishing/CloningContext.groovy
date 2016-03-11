package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.RelationshipDirection

class CloningContext extends PublishingContext<CloningContext> {

    private final DataModel destinationDataModel
    private final Set<Long> imports = []

    CloningContext(DataModel source, DataModel destination) {
        super(source)
        this.destinationDataModel = destination
    }

    static CloningContext create(DataModel source, DataModel destination) {
        new CloningContext(source, destination)
    }

    DataModel getDestination() {
        return destinationDataModel
    }

    void resolvePendingRelationships() {
        super.resolvePendingRelationships()
        for (Long importId in imports) {
            destinationDataModel.addToImports DataModel.get(importId)
        }
    }

    protected CopyAssociationsAndRelationships createCopyTask(CatalogueElement draft, CatalogueElement oldVersion) {
        new CopyAssociationsAndRelationships(draft, oldVersion, this, true, RelationshipDirection.OUTGOING)
    }

    public CatalogueElement findExisting(CatalogueElement published) {
        Long publishedId = published.getId()

        List<Relationship> existing = Relationship.where {
            source.id == publishedId && relationshipType == RelationshipType.originType && destination.dataModel == destinationDataModel
        }.list(max: 1, sort: 'outgoingIndex')

        if (!existing) {
            return null
        }

        CatalogueElement clone = existing.first().destination
        addResolution(published, clone)
        return clone
    }

    public void addImport(DataModel dataModel) {
        imports.add(dataModel.getId())
    }
}
