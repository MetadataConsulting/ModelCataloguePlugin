package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
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

    @Override
    void afterDraftPersisted(CatalogueElement draft) {
        super.afterDraftPersisted(draft)
        if (dataType) {
            (draft as DataElement).dataType = dataType
            FriendlyErrors.failFriendlySave(draft)
        }
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.containedIn).add(this.dataModel)
    }

    @Override
    List<String> getInheritedAssociationsNames() {
        ['dataType']
    }
}
