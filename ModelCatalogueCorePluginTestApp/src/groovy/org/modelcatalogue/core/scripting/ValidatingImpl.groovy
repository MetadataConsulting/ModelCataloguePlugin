package org.modelcatalogue.core.scripting

import groovy.transform.CompileStatic

@CompileStatic
class ValidatingImpl implements Validating {
    String implicitRule
    String explicitRule
    List<Validating> bases

    static  ValidatingImpl of(Validating el) {
        ValidatingImpl validating = new ValidatingImpl()
        validating.with {
            implicitRule = el.implicitRule
            explicitRule = el.explicitRule
            bases = el.bases.collect { Validating obj ->
                ValidatingImpl.of(obj)
            } as List<Validating>
        }
        validating
    }
}
