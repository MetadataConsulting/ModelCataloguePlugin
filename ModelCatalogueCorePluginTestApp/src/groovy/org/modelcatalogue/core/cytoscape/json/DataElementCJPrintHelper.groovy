package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship

/**
 * Created by james on 27/04/2017.
 */
@Singleton
class DataElementCJPrintHelper extends CatalogueElementCJPrintHelper<DataElement> {
    final String typeName = "DataElement"
    @Override
    void printElement(DataElement dataElement,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {
        super.printElement(dataElement, context, this.typeName, relationship, recursively)
        if(recursively) {
            if (dataElement.dataType) {
                dispatch(dataElement.dataType, context, null)
                // adding an edge that doesn't have a relationship behind it...
                // the relName and relTypeName are somewhat arbitrary.
                def sourceId = dataElement.getDefaultModelCatalogueId(false)
                def destinationId = dataElement.dataType.getDefaultModelCatalogueId(false)
                def relName = "type"
                def relTypeName = "elementType"
                def data = ["id": sourceId+relTypeName+destinationId,
                            "name": relName,
                            "type": relTypeName,
                            "source": sourceId,
                            "target": destinationId]
                context.listOfEdges << ["data": data]
            }
        }
    }
}
