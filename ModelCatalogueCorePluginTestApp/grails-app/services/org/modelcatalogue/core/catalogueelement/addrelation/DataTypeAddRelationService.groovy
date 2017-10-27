package org.modelcatalogue.core.catalogueelement.addrelation
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.DataTypeGormService

@CompileStatic
class DataTypeAddRelationService extends AbstractAddRelationService {

    DataTypeGormService dataTypeGormService

    @Override
    CatalogueElement findById(Long id) {
        dataTypeGormService.findById(id)
    }
}
