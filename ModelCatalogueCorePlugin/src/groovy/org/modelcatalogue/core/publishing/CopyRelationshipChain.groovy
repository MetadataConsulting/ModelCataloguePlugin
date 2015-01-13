package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

class CopyRelationshipChain extends PublishingChain {

    final CatalogueElement oldVersion


    private CopyRelationshipChain(CatalogueElement published, CatalogueElement oldVersion) {
        super(published)
        this.oldVersion = oldVersion
    }

    static CopyRelationshipChain create(CatalogueElement published, CatalogueElement oldVersion) {
        return new CopyRelationshipChain(published, oldVersion)
    }

    CatalogueElement run(Publisher<CatalogueElement> publisher) {
        if (published.published || isUpdatingInProgress(published)) {
            return published
        }

        if (published.status != ElementStatus.DRAFT) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.must.be.draft', 'Element is not draft!')
            return published
        }

        startUpdating()

        for (CatalogueElement element in required) {
            if (!element.published) {
                return rejectRequiredDependency(element)
            }
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (element.id in processed || isUpdatingInProgress(element)) {
                    continue
                }
                processed << element.id
                CatalogueElement finalized = element.publish(publisher)
                if (finalized.hasErrors()) {
                    return rejectFinalizationDependency(finalized)
                }
            }
        }
        return doPublish(publisher)
    }

    private CatalogueElement doPublish(Publisher<CatalogueElement> archiver) {
        published.status = ElementStatus.FINALIZED

        if (published.latestVersionId) {
            List<CatalogueElement> previousFinalized = published.getClass().findAllByLatestVersionId(published.latestVersionId)
            for (CatalogueElement e in previousFinalized) {
                if (e != published) {
                    archiver.archive(e)
                }
            }
        }

        published.save()
        published
    }


    private static <E extends CatalogueElement> E duplicateVersionUnspecificRelationships(E draft, E element) {
        for (Relationship r in element.incomingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            Relationship rel = draft.createLinkFrom(r.source, r.relationshipType)
            rel.ext = r.ext
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || r.relationshipType.versionSpecific) continue
            Relationship rel = draft.createLinkTo(r.destination, r.relationshipType)
            rel.ext = r.ext
        }

        draft
    }

    private static <E extends CatalogueElement> E copyVersionSpecificRelationships(E draft, E element) {
        def supersession = RelationshipType.readByName('supersession')

        for (Relationship r in element.incomingRelationships) {
            if (r.archived || !r.relationshipType.versionSpecific || r.relationshipType == supersession) continue
            Relationship rel = draft.createLinkFrom(preferDraft(r.source), r.relationshipType)
            rel.ext = r.ext
        }

        for (Relationship r in element.outgoingRelationships) {
            if (r.archived || !r.relationshipType.versionSpecific || r.relationshipType == supersession) continue
            Relationship rel = draft.createLinkTo(preferDraft(r.destination), r.relationshipType)
            rel.ext = r.ext
        }

        draft
    }

    private static CatalogueElement preferDraft(CatalogueElement element) {
        if (!element) {
            return element
        }
        if (isDraft(element) || isUpdatingInProgress(element)) {
            return element
        }
        if (!element.latestVersionId) {
            return element
        }
        CatalogueElement draft = element.class.findByLatestVersionIdAndStatusInList(element.latestVersionId, [ElementStatus.UPDATED, ElementStatus.DRAFT], [sort: 'versionNumber', order: 'desc'])
        if (draft) {
            return draft
        }
        return element
    }

    private static isDraft(CatalogueElement element) {
        element.status == ElementStatus.DRAFT
    }

}
