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
        addDataClassFields(ret, el)
        return ret
    }

    static void addDataClassFields(Map jsonReturnObject, Object dataClass) {
        jsonReturnObject.referenceTypes = [count: dataClass.countReferringDataTypes(), itemType: ReferenceType.name, link: "/${GrailsNameUtils.getPropertyName(dataClass.getClass())}/$dataClass.id/referenceType"]
        jsonReturnObject.content = [count: dataClass.countContains() + dataClass.countParentOf(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(dataClass.getClass())}/$dataClass.id/content"]
    }

}




