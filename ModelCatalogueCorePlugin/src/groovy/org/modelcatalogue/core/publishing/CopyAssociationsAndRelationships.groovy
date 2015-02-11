package org.modelcatalogue.core.publishing

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection

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


    void copyClassifications() {
        for (Classification classification in element.classifications) {
            draft.addToClassifications(DraftContext.preferDraft(classification))
        }
    }

    void copyRelationships() {
        if (classificationOnly) {
            return
        }

        copyRelationshipsInternal(RelationshipDirection.INCOMING)
        copyRelationshipsInternal(RelationshipDirection.OUTGOING)

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


    void copyRelationshipsInternal(RelationshipDirection direction) {
        RelationshipType supersession =  RelationshipType.readByName('supersession')
        RelationshipType classification = RelationshipType.readByName('classification')

        List<Relationship> toRemove = []

        def relationships = direction == RelationshipDirection.INCOMING ? element.incomingRelationships : element.outgoingRelationships

        for (Relationship r in relationships) {
            if (r.relationshipType == supersession || r.relationshipType == classification) continue
            Relationship created
            if (direction == RelationshipDirection.INCOMING) {
                if (r.archived && r.relationshipType.versionSpecific) {
                    // e.g. don't copy archived parents, but copy children (that's why this is in the incoming branch)
                    continue
                }
                created = relationshipService.link(DraftContext.preferDraft(r.source), draft, r.relationshipType, r.classification, r.archived)
            } else {
                created = relationshipService.link(draft, DraftContext.preferDraft(r.destination), r.relationshipType, r.classification, r.archived)
            }
            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship contains errors", created.errors))
            }
            created.ext = r.ext
            if (isOverriding(created, r)) {
                toRemove << r
            }
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
}
