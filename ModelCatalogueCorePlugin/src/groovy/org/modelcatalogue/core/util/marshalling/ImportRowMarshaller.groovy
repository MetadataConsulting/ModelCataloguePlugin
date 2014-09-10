package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.ImportRow

class ImportRowMarshaller extends AbstractMarshallers {

    ImportRowMarshaller() {
        super(ImportRow)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = [
                id: el.id,
                elementType: el.class.name,
                conceptualDomainDescription: el.conceptualDomainDescription,
                conceptualDomainName: el.conceptualDomainName,
                containingModelCode: el.containingModelCode,
                containingModelName: el.containingModelName,
                dataElementCode: el.dataElementCode,
                dataElementDescription: el.dataElementDescription,
                dataElementName: el.dataElementName,
                dataType: el.dataType,
                measurementSymbol: el.measurementSymbol,
                measurementUnitName: el.measurementUnitName,
                metadata: el.metadata,
                parentModelCode: el.parentModelCode,
                parentModelName: el.parentModelName,
                rowActions: el.rowActions,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id"
                ]
        ret
    }
}




