package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.FriendlyErrors

/**
 * A data element is an atomic unit of data
 * i.e. xml  <xs:element name="title" />
 */
class DataElement extends CatalogueElement {

    DataType dataType

    static constraints = {
        dataType nullable: true, fetch: 'join'
    }

    static relationships = [
        incoming: [containment: 'containedIn', involvedness: 'involvedIn',  tag: 'isTaggedBy']
    ]

    static fetchMode = [dataType: 'eager']

    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain).add(this.dataType)
    }

    List<CatalogueElement> collectExternalDependencies() {
        if (dataType && dataType.dataModel != dataModel) {
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
    Iterable<String> getInheritedAssociationsNames() {
        Iterables.concat(super.inheritedAssociationsNames, ImmutableSet.of('dataType'))
    }
}
