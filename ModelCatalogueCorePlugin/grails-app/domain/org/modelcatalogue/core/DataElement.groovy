package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.FriendlyErrors

/*
* A data element is an atomic unit of data
* i.e. xml  <xs:element name="title" />
*
* */

class DataElement extends CatalogueElement {

    DataType dataType

    static constraints = {
        dataType nullable: true, fetch: 'join'
    }

    static relationships = [
            incoming: [containment: 'containedIn'],
    ]

    static fetchMode = [dataType: 'eager']

    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain).add(this.dataType)
    }

    List<CatalogueElement> collectExternalDependencies() {
        if (dataType?.dataModel != dataModel) {
            return [dataType]
        }
        return super.collectExternalDependencies()
    }

    @Override
    void afterDraftPersisted(CatalogueElement draft, PublishingContext context) {
        super.afterDraftPersisted(draft, context)
        if (dataType) {
            (draft as DataElement).dataType = dataType
            FriendlyErrors.failFriendlySave(draft)
        }
    }

    @Override
    List<String> getInheritedAssociationsNames() {
        ['dataType']
    }

    @Override
    Long getFirstParentId() {
        return getContainedIn().find { it.getDataModelId() == getDataModelId() }?.getId() ?: super.getFirstParentId()
    }
}
