package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService

/**
 * Created by ladin on 15.01.15.
 */
class PrintContext {

    ClassificationService classificationService
    ModelService modelService


    boolean idIncludeVersion
    boolean noHref

    Classification currentClassification
    Set<Long> idsOfPrinted = []

    PrintContext(ClassificationService classificationService, ModelService modelService) {
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