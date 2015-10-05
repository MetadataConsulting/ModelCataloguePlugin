package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ReferenceType

class DataClassMarshaller extends CatalogueElementMarshaller {

    DataClassMarshaller() {
        super(DataClass)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object el) {
        Map<String, Object> ret = super.prepareJsonMap(el)
        ret.referenceTypes = [count: el.countReferringDataTypes(), itemType: ReferenceType.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/referenceType"]
        ret.content = ret.parentOf
        return ret
    }

}




