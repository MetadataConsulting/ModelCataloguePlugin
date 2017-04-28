package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Relationship

/**
 * Created by james on 28/04/2017.
 */
@Singleton
class MeasurementUnitCJPrintHelper extends CatalogueElementCJPrintHelper<MeasurementUnit> {

    final String typeName = "MeasurementUnit"
    @Override
    void printElement(MeasurementUnit measurementUnit,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {

        super.printElement(measurementUnit, context, this.typeName, relationship, recursively)
    }
}
