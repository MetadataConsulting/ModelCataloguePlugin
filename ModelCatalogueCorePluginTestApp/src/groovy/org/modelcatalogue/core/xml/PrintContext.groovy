package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataClassService

/** PrintContext keeps track of printed elements and contains some settings for the printing.*/
class PrintContext {

    DataModelService dataModelService
    /** Seems like Data Classes used to be called Models? */
    DataClassService modelService

    boolean idIncludeVersion
    boolean noHref
    /** Whether to print same elements more than once, as they appear in the structure explored */
    boolean repetitive

    DataModel currentDataModel
    /** The data model within which to print */
    DataModel keepInside
    Set<Long> idsOfPrinted = []

    Set<String> typesUsed = new TreeSet<String>()
    Set<String> policiesUsed = new TreeSet<String>()

    PrintContext(DataModelService dataModelService, DataClassService modelService) {
        this.dataModelService = dataModelService
        this.modelService = modelService
    }

    void markAsPrinted(CatalogueElement element) {
        idsOfPrinted << element.id
    }

    void removeFromPrinted(CatalogueElement element) {
        idsOfPrinted.remove(element.id)
    }

    boolean wasPrinted(CatalogueElement element) {
        idsOfPrinted.contains(element.id)
    }

    boolean printOnlyReference(CatalogueElement catalogueElement) {
        if (wasPrinted(catalogueElement)) {
            return true
        }
        if (!keepInside) {
            return false
        }
        if (!catalogueElement.dataModel) {
            return false
        }
        if (catalogueElement.dataModel == keepInside) {
            return false
        }
        if (catalogueElement == keepInside) {
            return false
        }
        return true
    }
}
