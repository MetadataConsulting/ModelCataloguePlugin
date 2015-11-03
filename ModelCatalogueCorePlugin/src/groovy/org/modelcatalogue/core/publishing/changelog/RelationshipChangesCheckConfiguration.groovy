package org.modelcatalogue.core.publishing.changelog

import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType

@PackageScope class RelationshipChangesCheckConfiguration {
    final CatalogueElement element
    final RelationshipType type

    boolean incoming = false
    boolean deep

    String changesSummaryHeading
    String newRelationshipNote
    String removedRelationshipNote

    static RelationshipChangesCheckConfiguration create(CatalogueElement element, RelationshipType type) {
        return new RelationshipChangesCheckConfiguration(element, type)
    }

    private RelationshipChangesCheckConfiguration(CatalogueElement element, RelationshipType type) {
        this.element = element
        this.type = type
    }

    RelationshipChangesCheckConfiguration withChangesSummaryHeading(String text) {
        this.changesSummaryHeading = text
        this
    }

    RelationshipChangesCheckConfiguration withNewRelationshipNote(String text) {
        this.newRelationshipNote = text
        this
    }

    RelationshipChangesCheckConfiguration withRemovedRelationshipNote(String text) {
        this.removedRelationshipNote = text
        this
    }

    RelationshipChangesCheckConfiguration withDeep(boolean deep) {
        this.deep = deep
        this
    }

    RelationshipChangesCheckConfiguration withIncoming(boolean incoming) {
        this.incoming = incoming
        this
    }





}
