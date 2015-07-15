package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.DataClassService

class PrintContext {

    ClassificationService classificationService
    DataClassService modelService


    boolean idIncludeVersion
    boolean noHref

    DataModel currentClassification
    Set<Long> idsOfPrinted = []

    Set<String> typesUsed = new TreeSet<String>()

    PrintContext(ClassificationService classificationService, DataClassService modelService) {
        this.classificationService = classificationService
        this.modelService = modelService
    }

    void markAsPrinted(CatalogueElement element) {
        idsOfPrinted << element.id
    }

    boolean wasPrinted(CatalogueElement element) {
        idsOfPrinted.contains(element.id)
    }

}