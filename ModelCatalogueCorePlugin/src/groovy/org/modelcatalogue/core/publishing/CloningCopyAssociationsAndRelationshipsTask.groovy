package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection

@Log4j
class CloningCopyAssociationsAndRelationshipsTask {

    private final CatalogueElement draft
    private final CatalogueElement element
    private final CloningContext context

    private RelationshipService relationshipService


    CloningCopyAssociationsAndRelationshipsTask(CatalogueElement draft, CatalogueElement element, CloningContext context) {
        this.draft = draft
        this.element = element
        this.context = context
        relationshipService = Holders.applicationContext.getBean(RelationshipService)
    }

    void copyRelationships(DataModel dataModel, Set<String> createdRelationshipHashes) {
        // copy only outgoing relationships as they define the structure
        copyRelationshipsInternal(dataModel, RelationshipDirection.OUTGOING, createdRelationshipHashes)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(HibernateProxyHelper.getClassWithoutInitializingProxy(draft).name) as GrailsDomainClass

        for (prop in domainClass.persistentProperties) {
            if (prop.association && (prop.manyToOne || prop.oneToOne) && element.hasProperty(prop.name) && prop.name != 'dataModel') {
                def value = element.getProperty(prop.name)
                if (value instanceof CatalogueElement) {
                    draft.setProperty(prop.name, context.preferClone(value))
                }
            }
        }
    }


    void copyRelationshipsInternal(DataModel dataModel, RelationshipDirection direction, Set<String> createdRelationshipHashes) {
        List<Relationship> toRemove = []

        relationshipService.eachRelationshipPartitioned(direction, element) { Relationship r ->
            if (r.relationshipType.system) {
                return
            }

            if (!r.relationshipType.versionSpecific) {
                // don't copy other relationships than version specific as these define the structure
                return
            }

            if (dataModel && r.relationshipType.versionSpecific && r.source.status != ElementStatus.DRAFT && dataModel != r.source.dataModel) {
                return
            }

            if (r.archived) {
                return
            }

            CatalogueElement otherSide
            String hash

            if (direction == RelationshipDirection.INCOMING) {
                otherSide = context.preferClone(r.source)
                hash = DraftContext.hashForRelationship(otherSide, draft, r.relationshipType)
            } else {
                otherSide = context.preferClone(r.destination)
                hash = DraftContext.hashForRelationship(draft, otherSide, r.relationshipType)
            }

            if (hash in createdRelationshipHashes) {
                return
            }

            RelationshipDefinitionBuilder definitionBuilder = direction == RelationshipDirection.INCOMING ? RelationshipDefinition.create(otherSide, draft, r.relationshipType) : RelationshipDefinition.create(draft, otherSide, r.relationshipType)

            definitionBuilder
                    .withArchived(r.archived)
                    .withDataModel(r.dataModel)
                    .withIncomingIndex(r.incomingIndex)
                    .withOutgoingIndex(r.outgoingIndex)
                    .withCombinedIndex(r.combinedIndex)
                    .withMetadata(r.ext)
                    .withSkipUniqueChecking(true)

            Relationship created = relationshipService.link definitionBuilder.definition

            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship ${created} contains errors", created.errors))
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

        CloningCopyAssociationsAndRelationshipsTask that = (CloningCopyAssociationsAndRelationshipsTask) o

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
