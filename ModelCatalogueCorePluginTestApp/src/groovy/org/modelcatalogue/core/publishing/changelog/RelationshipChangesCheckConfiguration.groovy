package org.modelcatalogue.core.publishing.changelog

import com.google.common.collect.Multimap
import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.audit.Change

@PackageScope class RelationshipChangesCheckConfiguration {
    final CatalogueElement element
    final RelationshipType type
    final Multimap<String, Change> byDestinationsAndSources

    boolean incoming = false
    boolean deep

    String changesSummaryHeading
    String newRelationshipNote
    String removedRelationshipNote

    static RelationshipChangesCheckConfiguration create(CatalogueElement element, RelationshipType type, Multimap<String, Change> byDestinationsAndSources) {
        return new RelationshipChangesCheckConfiguration(element, type, byDestinationsAndSources)
    }

    private RelationshipChangesCheckConfiguration(CatalogueElement element, RelationshipType type, Multimap<String, Change> byDestinationsAndSources) {
        this.element = element
        this.type = type
        this.byDestinationsAndSources = byDestinationsAndSources
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


    List<CatalogueElement> getRelations() {
        incoming ? element.getIncomingRelationsByType(type) : element.getOutgoingRelationsByType(type)
    }

    Set<Change> getChanges(CatalogueElement element) {
        byDestinationsAndSources.removeAll("${incoming ? 'in' : 'out'}:${type.name}:${element.getLatestVersionId() ?: element.getId()}".toString())
    }

    Set<Set<Change>> getOtherChanges() {
        byDestinationsAndSources.asMap().entrySet().findAll { it.key.startsWith("${incoming ? 'in' : 'out'}:${type.name}:")}.collect { it.value  as Set<Change> } as Set<Set<Change>>
    }

    List<CatalogueElement> getNestedRelations() {
        incoming ? element.getIncomingRelationsByType(type) : element.getOutgoingRelationsByType(type)
    }

}
