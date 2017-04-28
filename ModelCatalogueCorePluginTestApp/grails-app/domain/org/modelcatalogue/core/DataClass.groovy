package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Legacy

class DataClass extends CatalogueElement {

    /** Originally there was only hierarchy which could be to either DataClasses or DataElements.
     * Then David wanted to get rid of hierarchy and call it containment.
     * But then it proved useful to have two sorts of relationships:
     * one from Classes to Elements, the other from Classes to Classes.
     * This was used in SQL procedures.
     * It seems like "incoming" are also outgoing relationships that are
     * the inverse of the real incoming relationships.
     * contextFor is inverse of appliedWithin from ValidationRules.
     */
    static relationships = [
            incoming: [hierarchy: 'childOf', ruleContext: 'contextFor'],
            outgoing: [containment: 'contains', hierarchy: 'parentOf']
    ]

    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain)
                .add(this.contains)
                .add(this.parentOf)
    }

    List<CatalogueElement> collectExternalDependencies() {
        List<CatalogueElement> ret = []
        ret.addAll this.contains.findAll { it.dataModel != dataModel }
        ret.addAll this.parentOf.findAll { it.dataModel != dataModel }
        ret
    }

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        referringDataTypes.collectEntries {
            if (toBeDeleted) {
                // if DataModel is going to be deleted, then ReferenceType needs to be from same DataModel
                if (it.dataModel != toBeDeleted)
                    return [(it): it.dataModel]
                else
                    return [:]
            } else {
                // if deletes DataClass, it should not be used anywhere
                return [(it): null]
            }
        }
    }

    @Override
    void setModelCatalogueId(String mcID) {
        super.setModelCatalogueId(Legacy.fixModelCatalogueId(mcID))
    }

    @Override
    String getDefaultModelCatalogueId(boolean withoutVersion = false) {
        // TODO: remove when the class is renamed
        return Legacy.fixModelCatalogueId(super.getDefaultModelCatalogueId(withoutVersion))
    }


    List<ReferenceType> getReferringDataTypes() {
        if (!readyForQueries) {
            return []
        }
        return ReferenceType.findAllByDataClass(this)
    }

    Long countReferringDataTypes() {
        if (!readyForQueries) {
            return 0
        }
        return ReferenceType.countByDataClass(this)
    }

    DataClass removeFromReferringDataTypes(ReferenceType domain) {
        domain.dataClass = null
        FriendlyErrors.failFriendlySave(domain)
        this
    }


    @Override
    Long getFirstParentId() {
        return getChildOf().find { it.getDataModelId() == getDataModelId() }?.getId() ?: super.getFirstParentId()
    }
}
