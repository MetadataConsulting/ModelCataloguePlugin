package org.modelcatalogue.core.cytoscape.json

import groovy.json.JsonBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.cytoscape.json.CJPrintContext

/**
 * Prints out some aspects of an element as JSON for Cytoscape.
 * The main entry point, using various PrintHelpers, starting with CatalogueElementCJPrintHelper.
 * Created by james on 24/04/2017.
 */
class CatalogueCytoscapeJsonPrinter {
    /** Given to the CJPrintContext... or not. */
    DataModelService dataModelService
    /** Given to the CJPrintContext */
    DataClassService dataClassService

    CatalogueCytoscapeJsonPrinter(DataModelService dataModelService, DataClassService dataClassService) {
        this.dataModelService = dataModelService
        this.dataClassService = dataClassService
    }
    /** Returns a Writable which will write a number of elements as JSON to a given writer.
    * @param elements elements to be written
    * @param contextConfigurer configures the CJPrintContext
    * @see org.modelcatalogue.core.xml.CatalogueXmlPrinter#bind
    */
    JsonBuilder bind (CatalogueElement element,
                   @DelegatesTo(CJPrintContext) Closure contextConfigurer = {}) {
        CJPrintContext context = new CJPrintContext(dataClassService)
        context.with contextConfigurer

        /** listOfNodes is a list of maps of this format, the following being one node:
        	    {"data" : {
		"id" : "http://metadata.org.uk/ontologies/cancer#Diagnosis",
		"name" : "Diagnosis"
	    },
         "position" : {
         "x" : 0,
         "y" : 0
         }
         */
        // It would be better not to have to worry about the layout...


        /** listOfEdges is a list of maps of this format, being a single edge
         *  {"data" : {
         "id" : "http://metadata.org.uk/ontologies/cancer#hasBasisOfDiagnosis",
         "name" : "hasBasisOfDiagnosis",
         "source" : "http://metadata.org.uk/ontologies/cancer#Diagnosis",
         "target" : "http://www.metadata.org.uk/ontologies/cosd/bod#BasisOfDiagnosis"}},
         */
        CatalogueElementCJPrintHelper.dispatch(element,
                                                context,
                                                null)
        def data = ["elements" : ["nodes" : context.listOfNodes,
                                  "edges": context.listOfEdges]
                    /*"style": [["selector":"edge",
                               "style": ["label": "data(name)"]]
                             ]*/
                    ]
        return new JsonBuilder(data)


    }

}
