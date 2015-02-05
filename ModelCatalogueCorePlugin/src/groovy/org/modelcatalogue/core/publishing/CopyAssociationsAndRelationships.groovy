package org.modelcatalogue.core.publishing

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors

class CopyAssociationsAndRelationships {

    private final CatalogueElement draft
    private final CatalogueElement element
    private final boolean classificationOnly

    CopyAssociationsAndRelationships(CatalogueElement draft, CatalogueElement element, boolean classificationOnly) {
        this.draft = draft
        this.element = element
        this.classificationOnly = classificationOnly
    }


    void copyClassifications() {
        for (Classification classification in element.classifications) {
            draft.addToClassifications(classification)
        }
    }

    void copyRelationships() {
        if (classificationOnly) {
            return
        }

        RelationshipType supersession =  RelationshipType.readByName('supersession')
        RelationshipType classification = RelationshipType.readByName('classification')

        List<Relationship> incomingToRemove = []
        List<Relationship> outgoingToRemove = []

        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType == supersession || r.relationshipType == classification) continue
            Relationship created = draft.createLinkFrom(DraftContext.preferDraft(r.source), r.relationshipType)
            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship contains errors", created.errors))
            }
            created.ext = r.ext
            if (isOverriding(created, r)) {
                incomingToRemove << r
            }
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType == supersession || r.relationshipType == classification) continue
            Relationship created = draft.createLinkTo(DraftContext.preferDraft(r.destination), r.relationshipType)
            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship contains errors", created.errors))
            }
            created.ext = r.ext
            if (isOverriding(created, r)) {
                outgoingToRemove << r
            }
        }

        for (Relationship r in incomingToRemove) {
            element.removeLinkFrom(r.source, r.relationshipType)
        }

        for (Relationship r in outgoingToRemove) {
            element.removeLinkTo(r.destination, r.relationshipType)
        }

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
