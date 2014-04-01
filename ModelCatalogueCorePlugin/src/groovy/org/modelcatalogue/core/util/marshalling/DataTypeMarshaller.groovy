package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ValueDomain

class DataTypeMarshaller extends CatalogueElementMarshallers {

    DataTypeMarshaller() {
        super(DataType)
    }

    DataTypeMarshaller(Class<? extends DataType> cls) {
        super(cls)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll valueDomains: [count: element.relatedValueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"]
        return ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        xml.build {
            valueDomains count: element.relatedValueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"
        }
    }



}




