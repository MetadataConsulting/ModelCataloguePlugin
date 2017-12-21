package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.persistence.MeasurementUnitGormService

@CompileStatic
class MeasurementUnitCatalogueElementService<T extends CatalogueElement> extends AbstractCatalogueElementService<MeasurementUnit> {

    MeasurementUnitGormService measurementUnitGormService

    @Override
    CatalogueElement findById(Long id) {
        measurementUnitGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(MeasurementUnit.class.name)
    }
}
