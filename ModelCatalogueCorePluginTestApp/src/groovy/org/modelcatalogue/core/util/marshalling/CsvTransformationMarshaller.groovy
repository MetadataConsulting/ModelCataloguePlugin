package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.dataarchitect.CsvTransformation

class CsvTransformationMarshaller extends AbstractMarshaller {

    CsvTransformationMarshaller() {
        super(CsvTransformation)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]



        return [
                id: el.id,
                name: el.name,
                version: el.version,
                elementType: el.class.name,
                description: el.description,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                columns:  el.columnDefinitions.collect { [source: CatalogueElementMarshaller.minimalCatalogueElementJSON(it.source), destination: CatalogueElementMarshaller.minimalCatalogueElementJSON(it.destination), header: it.header] },
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
        ]
    }

}




