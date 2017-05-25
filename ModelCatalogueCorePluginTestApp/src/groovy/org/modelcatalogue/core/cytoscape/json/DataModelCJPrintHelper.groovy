package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.DataModelFilter

/**
 * Helper for printing DataModels to Cytoscape JSON.
 * Created by james on 25/04/2017.
 */
@Singleton
class DataModelCJPrintHelper extends CatalogueElementCJPrintHelper<DataModel> {

    final String typeName = "DataModel"
    @Override
    void printElement(DataModel dataModel,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {
        super.printElement(dataModel, context, this.typeName, relationship, recursively)
        // not doing revision notes or policy
        if (recursively) {
            def topLevelDataClasses = context.dataClassService.
                getTopLevelDataClasses(DataModelFilter.
                    includes(dataModel), [:]).items
            for (DataClass dataClass in
                topLevelDataClasses) {
                dispatch(dataClass, context, null)
                // adding an edge that doesn't have a relationship behind it...
                // the relName and relTypeName are somewhat arbitrary.
                def sourceId = dataModel.getDefaultModelCatalogueId(false)
                def destinationId = dataClass.getDefaultModelCatalogueId(false)
                def relName = "hasClass"
                def relTypeName = "modelClass"
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
