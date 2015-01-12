package org.modelcatalogue.core.publishing

import grails.util.Holders

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

class CopyAssociationsAndRelationships implements Runnable {

    private final CatalogueElement draft
    private final CatalogueElement element

    CopyAssociationsAndRelationships(CatalogueElement draft, CatalogueElement element) {
        this.draft = draft
        this.element = element
    }


    @Override
    void run() {
        RelationshipType supersession =  RelationshipType.readByName('supersession')
        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType == supersession) continue
            draft.createLinkFrom(DraftContext.preferDraft(r.source), r.relationshipType)
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType == supersession) continue
            draft.createLinkTo(DraftContext.preferDraft(r.destination), r.relationshipType)
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
