package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.CatalogueElement

/**
 * Similar to xml PrintContext but simplified and will carry the print result.
 * @see org.modelcatalogue.core.xml.PrintContext
 * Created by james on 27/04/2017.
 */
class CJPrintContext {

    // not using dataModelService as in xml.PrintContext
    /** used for getTopLevelDataClasses method in DataModelPrintHelper */
    DataClassService dataClassService

    CJPrintContext(DataClassService dataClassService) {
        this.dataClassService = dataClassService
    }

    // not using idIncludeVersion or noHref or repetitive

    DataModel keepInside
    //not using currentDataModel

    Set<Long> idsOfPrinted = new TreeSet<Long>()
    // not using relationshipTypesUsed or policiesUsed

    /** The list of nodes which will be printed. The nodes will be in a certain format for cytoscape. */
    def listOfNodes = []
    /** The list of edges which will be printed */
    def listOfEdges = []

    // implements a set interface... somewhat redundantly.
    void markAsPrinted(CatalogueElement element) { idsOfPrinted << element.id }
    void removeFromPrinted(CatalogueElement element) {idsOfPrinted.remove(element.id)}
    boolean wasPrinted(CatalogueElement element) {idsOfPrinted.contains(element.id)}

    /**
     * @param element
     * @return Whether to print element recursively: if we're not keeping inside a particular model,
     * or if the element is in the model we want to keep inside.
     */
    boolean printRecursively(CatalogueElement element) {
        return !keepInside || !element.dataModel ||
            element.dataModel == keepInside || catalogueElement == keepInside
    }

}
