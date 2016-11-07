package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship

class DataClassMarshaller extends CatalogueElementMarshaller {

    DataClassMarshaller() {
        super(DataClass)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object el) {
        Map<String, Object> ret = super.prepareJsonMap(el)
        ret.referenceTypes = [count: el.countReferringDataTypes(), itemType: ReferenceType.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/referenceType"]
        ret.content = [count: el.countContains() + el.countParentOf(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        return ret
    }

}




