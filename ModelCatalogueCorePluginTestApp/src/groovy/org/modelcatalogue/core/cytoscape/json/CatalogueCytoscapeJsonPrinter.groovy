package org.modelcatalogue.core.cytoscape.json

import groovy.json.JsonBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.xml.PrintContext

/**
 * Prints out some aspects of an element as JSON for Cytoscape.
 * Created by james on 24/04/2017.
 */
class CatalogueCytoscapeJsonPrinter {
    /** Given to the PrintContext */
    DataModelService dataModelService
    /** Given to the PrintContext */
    DataClassService dataClassService

    CatalogueCytoscapeJsonPrinter(DataModelService dataModelService, DataClassService dataClassService) {
        this.dataModelService = dataModelService
        this.dataClassService = dataClassService
    }
    /** Returns a Writable which will write a number of elements as JSON to a given writer.
    * @param elements elements to be written
    * @param contextConfigurer configures the PrintContext
    * @see org.modelcatalogue.core.xml.CatalogueXmlPrinter#bind
    */
    JsonBuilder bind (CatalogueElement element,
                   @DelegatesTo(PrintContext) Closure contextConfigurer = {}) {
        PrintContext context = new PrintContext(dataModelService, dataClassService)
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
        def elementId = element.getDefaultModelCatalogueId(false)
        def listOfNodes = [["data": ["id": elementId,
                                        "name": element.name ],
                            "position": ["x": 0, "y": 0]]
                            ]

        /** listOfEdges is a list of maps of this format, being a single edge
         *  {"data" : {
         "id" : "http://metadata.org.uk/ontologies/cancer#hasBasisOfDiagnosis",
         "name" : "hasBasisOfDiagnosis",
         "source" : "http://metadata.org.uk/ontologies/cancer#Diagnosis",
         "target" : "http://www.metadata.org.uk/ontologies/cosd/bod#BasisOfDiagnosis"}},
         */
        def listOfEdges = []

        addMetadata(element,listOfNodes, listOfEdges)

        CatalogueElementCJPrintHelper.printElement(element: element,
                                                listOfNodes: listOfNodes,
                                                listOfEdges: listOfEdges)
        def data = ["elements" : ["nodes" : listOfNodes, "edges": listOfEdges],
                    "style": [["selector":"edge",
                               "style": ["label": "data(name)"]]
                             ]
                    ]
        return new JsonBuilder(data)


    }
    /**
     * Add metadata from element to listOfNodes and listOfEdges
     * @param element
     * @param listOfNodes
     * @param listOfEdges
     * @return
     */
    def addMetadata(element, listOfNodes, listOfEdges) {
        def elementId = element.getDefaultModelCatalogueId(false)
        if (element.ext) {
            listOfNodes.addAll element.ext.collect { key, value ->
                ["data": ["id": elementId+key+value,
                          "name": value],
                 "position": ["x": 0, "y": 0]]
            }
        }
        if (element.ext) {
            listOfEdges.addAll element.ext.collect { key, value ->
                ["data": ["id": elementId+key,
                          "name": new URI(key).getFragment(), // get the last segment of the pathname
                          "source": elementId,
                          "target": elementId+key+value]]
            }
        }
    }
}
