package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration

/**
 * Helper for printing DataTypes to Cytoscape JSON.
 * Created by james on 28/04/2017.
 */
@Singleton
class DataTypeCJPrintHelper extends CatalogueElementCJPrintHelper<DataType> {
    final String typeName = "DataType"
    @Override
    void printElement(DataType dataType,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {

        super.printElement(dataType, context, this.typeName, relationship, recursively)
        // The following block comes from the XML printer and should be adapted for JSON printing.
        // TODO: Adapt printing of dataType.regexDef, dataType.rule, and a big one: dataType.enumerations
        /*if (dataType.regexDef) {
            markupBuilder.regex dataType.regexDef
        } else if (dataType.rule) {
            markupBuilder.rule dataType.rule
        }
        if (dataType instanceof EnumeratedType && dataType.enumerations) {
            markupBuilder.enumerations {
                for (Enumeration entry in dataType.enumerationsObject) {
                    if (entry.value) {
                        if (entry.deprecated) {
                            enumeration(value: entry.key, id: entry.id, deprecated: true, entry.value)
                        } else {
                            enumeration(value: entry.key, id: entry.id, entry.value)
                        }
                    } else {
                        if (entry.deprecated) {
                            enumeration(value: entry.key, id: entry.id, deprecated: true)
                        } else {
                            enumeration(value: entry.key, id: entry.id)
                        }
                    }
                }
            }
        }*/
        if (recursively) {
            if (dataType instanceof ReferenceType && dataType.dataClass) {
                if (dataType.dataClass) {
                    dispatch(dataType.dataClass, context, null)
                    // adding an edge that doesn't have a relationship behind it...
                    // the relName and relTypeName are somewhat arbitrary.
                    def sourceId = dataType.getDefaultModelCatalogueId(false)
                    def destinationId = dataType.dataClass.getDefaultModelCatalogueId(false)
                    def relName = "references"
                    def relTypeName = "typeReferencesClass"
                    def data = ["id": sourceId+relTypeName+destinationId,
                                "name": relName,
                                "type": relTypeName,
                                "source": sourceId,
                                "target": destinationId]
                    context.listOfEdges << ["data": data]
                }
            }
            if (dataType instanceof PrimitiveType && dataType.measurementUnit) {
                if (dataType.measurementUnit) {
                    dispatch(dataType.measurementUnit, context, null)
                    // adding an edge that doesn't have a relationship behind it...
                    // the relName and relTypeName are somewhat arbitrary.
                    def sourceId = dataType.getDefaultModelCatalogueId(false)
                    def destinationId = dataType.measurementUnit.getDefaultModelCatalogueId(false)
                    def relName = "primitive"
                    def relTypeName = "typeMeasurementUnit"
                    def data = ["id"    : sourceId + relTypeName + destinationId,
                                "name"  : relName,
                                "type"  : relTypeName,
                                "source": sourceId,
                                "target": destinationId]
                    context.listOfEdges << ["data": data]
                }
            }
        }
    }
}
