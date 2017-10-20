package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import org.modelcatalogue.core.publishing.PublishingChain

class ReferenceType extends DataType {

    DataClass dataClass

    static constraints = {
        dataClass nullable: true
    }

    static mapping = {
        dataClass fetch: 'join'
    }


    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain).add(this.dataClass)
    }

    List<CatalogueElement> collectExternalDependencies() {
        if (dataClass && dataClass.dataModel != dataModel) {
            return [dataClass]
        }
        return super.collectExternalDependencies()
    }


    Iterable<String> getInheritedAssociationsNames() { Iterables.concat(super.inheritedAssociationsNames, ImmutableSet.of('dataClass')) }

}
