package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataClassService

class PrintContext {

    DataModelService dataModelService
    DataClassService modelService


    boolean idIncludeVersion
    boolean noHref

    DataModel currentClassification
    Set<Long> idsOfPrinted = []

    Set<String> typesUsed = new TreeSet<String>()

    PrintContext(DataModelService dataModelService, DataClassService modelService) {
        this.dataModelService = dataModelService
        this.modelService = modelService
    }

    void markAsPrinted(CatalogueElement element) {
        idsOfPrinted << element.id
    }

    boolean wasPrinted(CatalogueElement element) {
        idsOfPrinted.contains(element.id)
    }

}