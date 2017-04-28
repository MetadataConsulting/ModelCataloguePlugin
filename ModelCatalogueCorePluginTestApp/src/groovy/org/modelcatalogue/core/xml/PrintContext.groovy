package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataClassService

/** PrintContext keeps track of printed elements and contains some settings for the printing.*/
class PrintContext {

    /** used for classified method in DataModelPrintHelper */
    DataModelService dataModelService
    /** used for getTopLevelDataClasses method in DataModelPrintHelper */
    DataClassService dataClassService

    /** used in ref and collectAttributes*/
    boolean idIncludeVersion
    /** used in ref */
    boolean noHref
    /** Whether to print same elements more than once, as they appear in the structure explored */
    boolean repetitive


    /** The data model within which to print. If this is set then
     * only CatalogueElements in this model can be printed. */
    DataModel keepInside
    /** Also in some way the data model within which to print!
     * Used in ref and collectAttributes.
     * Even if keepInside is null which means we want to print things other models in full,
     * the way things are now, this would prevent a different model from being printed itself,
     * although non-model CatalogueElements in a different model could be printed.*/
    DataModel currentDataModel
    Set<Long> idsOfPrinted = []

    Set<String> relationshipTypesUsed = new TreeSet<String>()
    Set<String> policiesUsed = new TreeSet<String>()

    PrintContext(DataModelService dataModelService, DataClassService dataClassService) {
        this.dataModelService = dataModelService
        this.dataClassService = dataClassService
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
        // && binds tighter than ||.
        // print only a reference to the element rather than the full thing if...
        return ( wasPrinted(catalogueElement) || // the element was printed or... (
            keepInside && // we want to keep inside a model and
            catalogueElement.dataModel && //  the element has a model and
            catalogueElement.dataModel != keepInside && //  the two are different and
            catalogueElement != keepInside)
        // the element is itself not the model we want to keep inside.) This seems like a vacuous check since for a model catalogueElement.dataModel should equal catalogueElement.
    }
}
