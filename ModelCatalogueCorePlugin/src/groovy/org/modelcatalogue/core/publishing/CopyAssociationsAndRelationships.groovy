package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.RelationshipDefinition
import org.modelcatalogue.core.util.builder.RelationshipDefinitionBuilder

@Log4j
class CopyAssociationsAndRelationships {

    private final CatalogueElement draft
    private final CatalogueElement element
    private final boolean classificationOnly
    private RelationshipService relationshipService

    CopyAssociationsAndRelationships(CatalogueElement draft, CatalogueElement element, boolean classificationOnly) {
        this.draft = draft
        this.element = element
        this.classificationOnly = classificationOnly
        relationshipService = Holders.applicationContext.getBean(RelationshipService)
    }


    void copyClassifications(Set<String> createdRelationshipHashes) {
        relationshipService.eachRelationshipPartitioned(RelationshipDirection.INCOMING, element, RelationshipType.classificationType) { Relationship r ->
            CatalogueElement source = DraftContext.preferDraft(r.source)

            String hash = DraftContext.hashForRelationship(source, draft, RelationshipType.classificationType)

            if (hash in createdRelationshipHashes) {
                return
            }

            RelationshipDefinitionBuilder definitionBuilder = RelationshipDefinition.create(source, draft, RelationshipType.classificationType)

            definitionBuilder
                    .withArchived(r.archived)
                    .withClassification(r.classification)
                    .withIncomingIndex(r.incomingIndex)
                    .withOutgoingIndex(r.outgoingIndex)
                    .withCombinedIndex(r.combinedIndex)
                    .withMetadata(r.ext)
                    .withSkipUniqueChecking(true)

            Relationship created = relationshipService.link definitionBuilder.definition

            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Cannot transfer classification", created.errors))
            }

            createdRelationshipHashes << hash
        }
    }

    void copyRelationships(Set<String> createdRelationshipHashes) {
        if (classificationOnly) {
            return
        }

        copyRelationshipsInternal(RelationshipDirection.INCOMING, createdRelationshipHashes)
        copyRelationshipsInternal(RelationshipDirection.OUTGOING, createdRelationshipHashes)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(draft.class.name) as GrailsDomainClass

        for (prop in domainClass.persistentProperties) {
            if (prop.association && (prop.manyToOne || prop.oneToOne)) {
                def value = element.getProperty(prop.name)
                if (value instanceof CatalogueElement) {
                    draft.setProperty(prop.name, DraftContext.preferDraft(value))
                }
            }
        }
    }


    void copyRelationshipsInternal(RelationshipDirection direction, Set<String> createdRelationshipHashes) {
        RelationshipType supersession =  RelationshipType.readByName('supersession')

        List<Relationship> toRemove = []

        relationshipService.eachRelationshipPartitioned(direction, element) { Relationship r ->
            if (r.relationshipType == supersession) {
                return
            }

            CatalogueElement otherSide
            String hash

            if (direction == RelationshipDirection.INCOMING) {
                otherSide = DraftContext.preferDraft(r.source)
                hash = DraftContext.hashForRelationship(otherSide, draft, r.relationshipType)
            } else {
                otherSide = DraftContext.preferDraft(r.destination)
                hash = DraftContext.hashForRelationship(draft, otherSide, r.relationshipType)
            }

            if (hash in createdRelationshipHashes) {
                return
            }


            if (direction == RelationshipDirection.INCOMING) {
                if (r.archived && r.relationshipType.versionSpecific) {
                    // e.g. don't copy archived parents, but copy children (that's why this is in the incoming branch)
                    return
                }
            }

            RelationshipDefinitionBuilder definitionBuilder = direction == RelationshipDirection.INCOMING ? RelationshipDefinition.create(otherSide, draft, r.relationshipType) : RelationshipDefinition.create(draft, otherSide, r.relationshipType)

            definitionBuilder
                    .withArchived(r.archived)
                    .withClassification(r.classification)
                    .withIncomingIndex(r.incomingIndex)
                    .withOutgoingIndex(r.outgoingIndex)
                    .withCombinedIndex(r.combinedIndex)
                    .withMetadata(r.ext)
                    .withSkipUniqueChecking(true)

            Relationship created = relationshipService.link definitionBuilder.definition

            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship contains errors", created.errors))
            }

            if (isOverriding(created, r)) {
                toRemove << r
            }

            createdRelationshipHashes << hash
        }

        for (Relationship r in toRemove) {
            if (direction == RelationshipDirection.INCOMING) {
                element.removeLinkFrom(r.source, r.relationshipType)
            } else {
                element.removeLinkTo(r.destination, r.relationshipType)
            }
        }
    }

    static boolean isOverriding(Relationship created, Relationship old) {
        if (!(created.source.status == ElementStatus.DRAFT && created.destination.status == ElementStatus.DRAFT)) {
            return false
        }
        if (old.source.status == ElementStatus.DRAFT && old.destination.status != ElementStatus.DRAFT) {
            return true
        }
        if (old.source.status != ElementStatus.DRAFT && old.destination.status == ElementStatus.DRAFT) {
            return true
        }
        return false
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CopyAssociationsAndRelationships that = (CopyAssociationsAndRelationships) o

        if (draft != that.draft) return false
        if (element != that.element) return false

        return true
    }

    int hashCode() {
        int result
        result = draft.hashCode()
        result = 31 * result + element.hashCode()
        return result
    }

    @Override
    String toString() {
        "Task Copy Relationships from $element to $draft"
    }
}
