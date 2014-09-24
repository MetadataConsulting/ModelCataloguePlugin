package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Classification

class ClassificationMarshaller extends CatalogueElementMarshallers {

    ClassificationMarshaller() {
        super(Classification)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace,
                    classifies: [count: element.classifies?.size() ?: 0, itemType: Classification.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/classifies"]
        return ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        xml.build {
            namespace element.namespace
            classifies count: element.classifies?.size() ?: 0, itemType: Classification.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/classifies"
        }
    }
}




