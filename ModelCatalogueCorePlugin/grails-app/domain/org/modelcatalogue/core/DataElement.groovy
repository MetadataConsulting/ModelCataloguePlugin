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

    ValueDomain valueDomain

    static constraints = {
        valueDomain nullable: true, fetch: 'join'
    }

    static relationships = [
            incoming: [containment: 'containedIn'],
    ]

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain.finalize(this)
        .add(this.valueDomain)
        .run(publisher)
    }

    @Override
    void afterDraftPersisted(CatalogueElement draft) {
        super.afterDraftPersisted(draft)
        if (valueDomain) {
            (draft as DataElement).valueDomain = valueDomain
            FriendlyErrors.failFriendlySave(draft)
        }
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.containedIn).add(this.classifications)
    }
}
