package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValidatingImpl

@CompileStatic
class ValidationRuleService {

    static transactional = false

    protected Validating validatingByCatalogueElement(CatalogueElement catalogueElement) {
        ValidatingImpl validating = null
        if ( catalogueElement instanceof DataElement ) {
            DataType dataType = ((DataElement) catalogueElement).dataType
            if ( dataType ) {
                validating = ValidatingImpl.of(dataType)
            }
        }
        if ( catalogueElement instanceof Validating ) {
            validating = ValidatingImpl.of(catalogueElement)
        }
        validating
    }
}
