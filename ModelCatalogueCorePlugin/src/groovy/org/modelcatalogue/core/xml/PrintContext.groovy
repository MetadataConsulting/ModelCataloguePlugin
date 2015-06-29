package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService
import org.modelcatalogue.core.RelationshipType

class PrintContext {

    ClassificationService classificationService
    ModelService modelService


    boolean idIncludeVersion
    boolean noHref

    Classification currentClassification
    Set<Long> idsOfPrinted = []

    Set<String> typesUsed = new TreeSet<String>()

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